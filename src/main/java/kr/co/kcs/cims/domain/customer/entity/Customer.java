package kr.co.kcs.cims.domain.customer.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "customers",
        indexes = {
            @Index(name = "idx_credit_grade", columnList = "creditGrade"),
            @Index(name = "idx_credit_grade_updated_at", columnList = "creditGradeUpdatedAt")
        })
@NoArgsConstructor
public class Customer extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private PersonalInfo personalInfo;

    @OneToMany(mappedBy = "customer")
    private final List<CreditTransaction> creditTransactions = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private CreditGrade creditGrade;

    private LocalDateTime creditGradeUpdatedAt;

    @Builder
    public Customer(PersonalInfo personalInfo, CreditGrade creditGrade, LocalDateTime creditGradeUpdatedAt) {
        this.personalInfo = personalInfo;
        this.creditGrade = creditGrade;
        this.creditGradeUpdatedAt = creditGradeUpdatedAt;
    }

    public void updateCreditGrade() {
        // TODO 신용 등급 갱신 로직 구현
    }

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
}
