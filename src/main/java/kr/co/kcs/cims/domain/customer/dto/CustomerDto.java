package kr.co.kcs.cims.domain.customer.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.soabase.recordbuilder.core.RecordBuilder;
import kr.co.kcs.cims.domain.customer.entity.Customer;
import kr.co.kcs.cims.domain.customer.entity.PersonalInfo;
import kr.co.kcs.cims.domain.customer.enums.CreditGrade;

/**
 * DTO for {@link Customer}
 */
@RecordBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public record CustomerDto(
        Long id,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean deleted,
        LocalDateTime deletedAt,
        PersonalInfoDto personalInfo,
        List<CreditTransactionDto> creditTransactions,
        CreditGrade creditGrade,
        LocalDateTime creditGradeUpdatedAt)
        implements Serializable {

    /**
     * DTO for {@link PersonalInfo}
     */
    @RecordBuilder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PersonalInfoDto(String name, LocalDate birthDate, String email, String phoneNumber)
            implements Serializable {

        public static PersonalInfoDto from(Customer customer) {
            return new PersonalInfoDto(
                    customer.getPersonalInfo().getName(),
                    customer.getPersonalInfo().getBirthDate(),
                    customer.getPersonalInfo().getEmail(),
                    customer.getPersonalInfo().getPhoneNumber());
        }
    }

    // DTO <- Customer
    public static CustomerDto from(Customer customer) {
        List<CreditTransactionDto> creditTransactionDtoList = customer.getCreditTransactions().stream()
                .map(CreditTransactionDto::from)
                .toList();

        return new CustomerDto(
                customer.getId(),
                customer.getCreatedAt(),
                customer.getUpdatedAt(),
                customer.isDeleted(),
                customer.getDeletedAt(),
                PersonalInfoDto.from(customer),
                creditTransactionDtoList,
                CreditGrade.findByGrade(customer.getCreditGrade()),
                customer.getCreditGradeUpdatedAt());
    }
}
