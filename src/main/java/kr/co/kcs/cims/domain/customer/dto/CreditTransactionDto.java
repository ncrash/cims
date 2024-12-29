package kr.co.kcs.cims.domain.customer.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
}
