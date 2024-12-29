package kr.co.kcs.cims.domain.customer.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import kr.co.kcs.cims.domain.customer.entity.Customer;
import kr.co.kcs.cims.domain.customer.entity.PersonalInfo;
import kr.co.kcs.cims.domain.customer.enums.CreditGrade;

public record CustomerRequestDto(
        @NotBlank(message = "이름은 필수입니다") @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다") String name,
        @NotNull(message = "생년월일은 필수입니다") @Past(message = "생년월일은 과거 날짜여야 합니다") LocalDate birthDate,
        @NotBlank(message = "이메일은 필수입니다")
                @Size(max = 100, message = "이메일은 100자를 초과할 수 없습니다")
                @Email(message = "올바른 이메일 형식이어야 합니다")
                String email,
        @NotBlank(message = "전화번호는 필수입니다")
                @Pattern(
                        regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$",
                        message = "올바른 휴대폰 번호 형식이어야 합니다 (예: 010-1234-5678)")
                String phoneNumber) {

    // DTO -> Customer
    public Customer toEntity() {
        PersonalInfo personalInfo = PersonalInfo.builder()
                .name(this.name)
                .birthDate(this.birthDate)
                .email(this.email)
                .phoneNumber(this.phoneNumber)
                .build();

        return Customer.builder()
                .personalInfo(personalInfo)
                .creditGrade(CreditGrade.GRADE_007)
                .creditGradeUpdatedAt(LocalDateTime.now())
                .build();
    }
}
