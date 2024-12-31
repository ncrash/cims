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
    public Customer(Long id, PersonalInfo personalInfo, CreditGrade creditGrade, LocalDateTime creditGradeUpdatedAt) {
        this.id = id;
        this.personalInfo = personalInfo;
        this.creditGrade = creditGrade;
        this.creditGradeUpdatedAt = creditGradeUpdatedAt;
    }

    public List<CreditTransaction> getCreditTransactions() {
        return Collections.unmodifiableList(creditTransactions);
    }

    public void updateCreditGrade(CreditGrade creditGrade) {
        // TODO verify 메소드 작성
        this.creditGrade = creditGrade;
    }

    // FIXME entity converter를 쓰는게 어떨지 검토
    public int getCreditGrade() {
        if (creditGrade == null) {
            return 0;
        }

        return creditGrade.getGradeNumber();
    }

    public void updatePersonalInfo(PersonalInfo personalInfo) {
        // TODO verify 메소드 구현
        // PersonalInfo 정보 업데이트 시 사전 검토되어야할 제약사항이 존재하는지 검토 후 업데이트 수행

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
}
