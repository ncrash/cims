package kr.co.kcs.cims.domain.customer.customer;

import java.time.LocalDateTime;
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
import lombok.Getter;

@Getter
@Entity
@Table(
        name = "customers",
        indexes = {
            @Index(name = "idx_credit_grade", columnList = "creditGrade"),
            @Index(name = "idx_credit_grade_updated_at", columnList = "creditGradeUpdatedAt")
        })
public class Customer extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private PersonalInfo personalInfo;

    @OneToMany(mappedBy = "customer")
    private List<CreditTransaction> creditTransactions;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private CreditGrade creditGrade;

    private LocalDateTime creditGradeUpdatedAt;

    public void updateCreditGrade() {
        // TODO 신용 등급 갱신 로직 구현
    }

    public int getCreditGrade() {
        if (creditGrade == null) {
            return 0;
        }

        return creditGrade.getGradeNumber();
    }
}
