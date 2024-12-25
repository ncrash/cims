package kr.co.kcs.cims.customer;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private LocalDate birthDate;
    private String email;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private CreditGrade creditGrade;

    private LocalDate lastUpdated;

    public void updateCreditGrade() {
        // TODO 신용 등급 갱신 로직 구현
    }
}
