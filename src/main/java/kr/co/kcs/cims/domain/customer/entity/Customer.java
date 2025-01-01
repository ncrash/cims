package kr.co.kcs.cims.domain.customer.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import kr.co.kcs.cims.domain.common.AbstractEntity;
import kr.co.kcs.cims.domain.customer.enums.CreditGrade;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@Table(
        name = "customers",
        indexes = {
            @Index(name = "idx_credit_grade", columnList = "creditGrade"),
            @Index(name = "idx_credit_grade_updated_at", columnList = "creditGradeUpdatedAt")
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE customers SET deleted = true, deleted_at = NOW() WHERE id = ? AND deleted = false")
@SQLRestriction("deleted = false")
@ToString(exclude = "creditTransactions", callSuper = true)
public class Customer extends AbstractEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String username;

    @Column(length = 255, nullable = false)
    private String password;

    @Embedded
    private PersonalInfo personalInfo;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<CreditTransaction> creditTransactions = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private CreditGrade creditGrade;

    private LocalDateTime creditGradeUpdatedAt;

    @Builder
    public Customer(
            Long id,
            String username,
            String password,
            PersonalInfo personalInfo,
            CreditGrade creditGrade,
            LocalDateTime creditGradeUpdatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.personalInfo = personalInfo;
        this.creditGrade = creditGrade;
        this.creditGradeUpdatedAt = creditGradeUpdatedAt;
    }

    public List<CreditTransaction> getCreditTransactions() {
        return Collections.unmodifiableList(creditTransactions);
    }

    public void updateCreditGrade(CreditGrade creditGrade) {
        verifyCreditGradeUpdate(creditGrade);

        this.creditGrade = creditGrade;
        this.creditGradeUpdatedAt = LocalDateTime.now(clock);
    }

    public int getCreditGrade() {
        if (creditGrade == null) {
            return 0;
        }

        return creditGrade.getGradeNumber();
    }

    public void updatePersonalInfo(PersonalInfo personalInfo) {
        this.personalInfo = personalInfo;
    }

    public void addTransaction(CreditTransaction transaction) {
        this.creditTransactions.add(transaction);
        transaction.setCustomer(this);
    }

    public void removeTransaction(CreditTransaction transaction) {
        if (transaction != null) {
            this.creditTransactions.remove(transaction);
            transaction.setCustomer(null); // 양방향 관계 해제
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    private void verifyCreditGradeUpdate(CreditGrade newGrade) {
        // null 체크
        if (newGrade == null) {
            throw new IllegalArgumentException("신용등급은 null일 수 없습니다.");
        }

        // 현재 등급이 없는 경우 (신규 고객)
        if (this.creditGrade == null) {
            return;
        }

        // 한 번에 2단계 이상 상승 불가
        if (newGrade.getGradeNumber() - this.creditGrade.getGradeNumber() > 2) {
            throw new IllegalArgumentException("신용등급은 한 번에 2단계까지만 상승할 수 있습니다.");
        }

        // 한 번에 3단계 이상 하락 불가
        if (this.creditGrade.getGradeNumber() - newGrade.getGradeNumber() > 3) {
            throw new IllegalArgumentException("신용등급은 한 번에 3단계까지만 하락할 수 있습니다.");
        }

        // 최근 3개월 내 등급 변경이 있었다면 변경 불가
        if (this.creditGradeUpdatedAt != null
                && this.creditGradeUpdatedAt.plusMonths(3).isAfter(LocalDateTime.now(clock))) {
            throw new IllegalArgumentException("신용등급은 최근 변경일로부터 3개월이 지나야 재변경이 가능합니다.");
        }
    }
}
