package kr.co.kcs.cims.domain.customer.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.co.kcs.cims.FreezeTime;
import kr.co.kcs.cims.domain.customer.enums.CreditGrade;
import kr.co.kcs.cims.domain.customer.enums.RepaymentStatus;
import kr.co.kcs.cims.domain.customer.enums.TransactionType;

class CustomerTest {

    @Test
    @DisplayName("고객 생성 테스트")
    void createCustomer() {
        // given
        PersonalInfo personalInfo = PersonalInfo.builder()
                .name("홍길동")
                .email("hong@test.com")
                .phoneNumber("010-1234-5678")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // when
        Customer customer = Customer.builder()
                .personalInfo(personalInfo)
                .creditGrade(CreditGrade.GRADE_007)
                .build();

        // then
        assertThat(customer).isNotNull();
        assertThat(customer.getPersonalInfo().getName()).isEqualTo("홍길동");
        assertThat(customer.getCreditGrade()).isEqualTo(7);
    }

    @Test
    @DisplayName("신용 거래 추가 테스트")
    void addCreditTransaction() {
        // given
        Customer customer = createTestCustomer();
        CreditTransaction transaction = CreditTransaction.builder()
                .type(TransactionType.LOAN)
                .amount(BigDecimal.valueOf(1000000))
                .status(RepaymentStatus.PENDING)
                .build();

        // when
        customer.addTransaction(transaction);

        // then
        assertThat(customer.getCreditTransactions()).hasSize(1);
        assertThat(customer.getCreditTransactions().getFirst().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(1000000));
        assertThat(transaction.getCustomer()).isEqualTo(customer);
    }

    @Test
    @DisplayName("신용 거래 제거 테스트")
    void removeCreditTransaction() {
        // given
        Customer customer = createTestCustomer();
        CreditTransaction transaction = CreditTransaction.builder()
                .type(TransactionType.LOAN)
                .amount(BigDecimal.valueOf(1000000))
                .status(RepaymentStatus.PAID)
                .build();
        customer.addTransaction(transaction);

        // when
        customer.removeTransaction(transaction);

        // then
        assertThat(customer.getCreditTransactions()).isEmpty();
        assertThat(transaction.getCustomer()).isNull();
    }

    @Test
    @DisplayName("개인정보 업데이트 테스트")
    void updatePersonalInfo() {
        // given
        Customer customer = createTestCustomer();
        PersonalInfo newInfo = PersonalInfo.builder()
                .name("김철수")
                .email("kim@test.com")
                .phoneNumber("010-9876-5432")
                .birthDate(LocalDate.of(1995, 12, 31))
                .build();

        // when
        customer.updatePersonalInfo(newInfo);

        // then
        assertThat(customer.getPersonalInfo().getName()).isEqualTo("김철수");
        assertThat(customer.getPersonalInfo().getEmail()).isEqualTo("kim@test.com");
    }

    @Test
    @DisplayName("신용등급 업데이트 - 정상 케이스")
    @FreezeTime("2024-01-24T00:00:00Z")
    void updateCreditGrade_Success(Clock clock) {
        // given
        Customer customer = createTestCustomer();
        customer.setClock(clock);
        customer.updateCreditGrade(CreditGrade.GRADE_007);

        // when - 3개월 후로 시간 이동
        Clock futureClock = Clock.fixed(Instant.parse("2024-05-01T00:00:00Z"), ZoneId.systemDefault());
        customer.setClock(futureClock);

        //  then
        assertThatCode(() -> customer.updateCreditGrade(CreditGrade.GRADE_008)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("신용등급 업데이트 - null 값 검증")
    void updateCreditGrade_NullValue() {
        // given
        Customer customer = createTestCustomer();

        // when & then
        assertThatThrownBy(() -> customer.updateCreditGrade(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("신용등급은 null일 수 없습니다.");
    }

    @Test
    @DisplayName("신용등급 업데이트 - 2단계 초과 상승 제한")
    void updateCreditGrade_ExceedUpgradeLimit() {
        // given
        Customer customer = createTestCustomer();
        customer.updateCreditGrade(CreditGrade.GRADE_007);

        // when & then
        assertThatThrownBy(() -> customer.updateCreditGrade(CreditGrade.GRADE_010))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("신용등급은 한 번에 2단계까지만 상승할 수 있습니다.");
    }

    @Test
    @DisplayName("신용등급 업데이트 - 5단계 초과 하락 제한")
    void updateCreditGrade_ExceedDowngradeLimit() {
        // given
        Customer customer = createTestCustomer();
        customer.updateCreditGrade(CreditGrade.GRADE_007);

        // when & then
        assertThatThrownBy(() -> customer.updateCreditGrade(CreditGrade.GRADE_001))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("신용등급은 한 번에 5단계까지만 하락할 수 있습니다.");
    }

    @Test
    @DisplayName("신용등급 업데이트 - 3개월 이내 재변경")
    void updateCreditGrade_TooFrequentUpdate() {
        // given
        Customer customer = createTestCustomer();
        customer.updateCreditGrade(CreditGrade.GRADE_007);

        // when & then
        customer.updateCreditGrade(CreditGrade.GRADE_009);
    }

    private Customer createTestCustomer() {
        PersonalInfo personalInfo = PersonalInfo.builder()
                .name("홍길동")
                .email("hong@test.com")
                .phoneNumber("010-1234-5678")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        return Customer.builder()
                .personalInfo(personalInfo)
                .creditGrade(CreditGrade.GRADE_007)
                .build();
    }
}
