package kr.co.kcs.cims.domain.customer.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.soabase.recordbuilder.core.RecordBuilder;
import kr.co.kcs.cims.domain.customer.entity.CreditTransaction;
import kr.co.kcs.cims.domain.customer.enums.RepaymentStatus;
import kr.co.kcs.cims.domain.customer.enums.TransactionType;

/**
 * DTO for {@link CreditTransaction}
 */
@RecordBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public record CreditTransactionDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean deleted,
        LocalDateTime deletedAt,
        Integer version,
        TransactionType type,
        BigDecimal amount,
        RepaymentStatus status,
        LocalDateTime transactionDate)
        implements Serializable {

    public static CreditTransactionDto from(CreditTransaction creditTransaction) {
        return new CreditTransactionDto(
                creditTransaction.getId(),
                creditTransaction.getCreatedAt(),
                creditTransaction.getUpdatedAt(),
                creditTransaction.isDeleted(),
                creditTransaction.getDeletedAt(),
                creditTransaction.getVersion(),
                creditTransaction.getType(),
                creditTransaction.getAmount(),
                creditTransaction.getStatus(),
                creditTransaction.getTransactionDate());
    }

    public CreditTransactionDto withStatus(RepaymentStatus newStatus) {
        return new CreditTransactionDto(
                id(),
                createdAt(),
                updatedAt(),
                deleted(),
                deletedAt(),
                version(),
                type(),
                amount(),
                newStatus,
                transactionDate());
    }
}
