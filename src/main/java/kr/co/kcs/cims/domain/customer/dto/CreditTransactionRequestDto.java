package kr.co.kcs.cims.domain.customer.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.co.kcs.cims.domain.customer.entity.CreditTransaction;
import kr.co.kcs.cims.domain.customer.enums.RepaymentStatus;
import kr.co.kcs.cims.domain.customer.enums.TransactionType;

public record CreditTransactionRequestDto(
        Long transactionId,
        @NotNull(message = "고객 ID는 필수입니다") Long customerId,
        @NotNull(message = "거래 유형은 필수입니다") TransactionType type,
        @NotNull(message = "거래 금액은 필수입니다") @Positive(message = "거래 금액은 양수여야 합니다") BigDecimal amount,
        @NotNull(message = "상환 상태는 필수입니다") RepaymentStatus status)
        implements Serializable {

    // DTO -> CreditTransaction
    public CreditTransaction toEntity() {
        return CreditTransaction.builder()
                .type(this.type)
                .amount(this.amount)
                .status(this.status)
                .build();
    }
}
