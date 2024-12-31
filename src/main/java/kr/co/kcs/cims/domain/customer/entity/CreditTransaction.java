package kr.co.kcs.cims.domain.customer.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import kr.co.kcs.cims.domain.common.AbstractEntity;
import kr.co.kcs.cims.domain.customer.enums.RepaymentStatus;
import kr.co.kcs.cims.domain.customer.enums.TransactionType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity
@Table(
        name = "credit_transactions",
        indexes = {
            @Index(name = "idx_status", columnList = "status"),
            @Index(name = "idx_created_at", columnList = "createdAt")
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE credit_transactions SET deleted = true, deleted_at = NOW() WHERE id = ? AND deleted = false")
@SQLRestriction("deleted = false")
@ToString(exclude = "customer", callSuper = true)
public class CreditTransaction extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Integer version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private TransactionType type; // 대출, 신용카드, 할부

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private RepaymentStatus status; // 완납, 연체

    @Builder
    public CreditTransaction(Customer customer, TransactionType type, BigDecimal amount, RepaymentStatus status) {
        this.customer = customer;
        this.type = type;
        this.amount = amount;
        this.status = status;
    }

    void setCustomer(Customer customer) {
        this.customer = customer;
    }

    // 거래일자가 필요한 경우 createdAt 필드 참조
    @Transient
    public LocalDateTime getTransactionDate() {
        return getCreatedAt();
    }

    public void changeStatus(RepaymentStatus status) {
        // TODO verify 메소드 구현
        this.status = status;
    }
}
