package kr.co.kcs.cims.domain.customer.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import kr.co.kcs.cims.domain.customer.entity.CreditTransaction;
import kr.co.kcs.cims.domain.customer.enums.RepaymentStatus;
import kr.co.kcs.cims.domain.customer.enums.TransactionType;

/**
 * DTO for {@link CreditTransaction}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CreditTransactionDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        TransactionType type,
        BigDecimal amount,
        RepaymentStatus status)
        implements Serializable {

    public static CreditTransactionDto from(CreditTransaction creditTransaction) {
        return new CreditTransactionDto(
                creditTransaction.getId(),
                creditTransaction.getCreatedAt(),
                creditTransaction.getUpdatedAt(),
                creditTransaction.getType(),
                creditTransaction.getAmount(),
                creditTransaction.getStatus());
    }

    // TODO kr.co.kcs.cims.domain.customer.dto.CreditTransactionDto.CreditRequest ->
    // kr.co.kcs.cims.domain.customer.dto.CreditTransactionRequestDto
    public record CreditRequest(
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
}
