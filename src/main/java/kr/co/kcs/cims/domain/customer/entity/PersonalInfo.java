package kr.co.kcs.cims.domain.customer.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PersonalInfo {
    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 20, nullable = false)
    private String phoneNumber;

    @Builder
    public PersonalInfo(LocalDate birthDate, String email, String phoneNumber, String name) {
        this.birthDate = birthDate;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.name = name;
    }
}
