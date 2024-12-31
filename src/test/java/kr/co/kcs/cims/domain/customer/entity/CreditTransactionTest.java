package kr.co.kcs.cims.domain.customer.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import kr.co.kcs.cims.domain.customer.enums.CreditGrade;
import kr.co.kcs.cims.domain.customer.enums.RepaymentStatus;
import kr.co.kcs.cims.domain.customer.enums.TransactionType;

@ExtendWith(MockitoExtension.class)
class CreditTransactionTest {

    @Test
    @DisplayName("신용 거래 생성 테스트")
    void createCreditTransaction() {
        // given
        Customer customer = createTestCustomer();

        // when
        CreditTransaction transaction = CreditTransaction.builder()
                .customer(customer)
                .type(TransactionType.LOAN)
                .amount(BigDecimal.valueOf(1000000))
                .status(RepaymentStatus.PENDING)
                .build();

        // then
        assertThat(transaction).isNotNull();
        assertThat(transaction.getType()).isEqualTo(TransactionType.LOAN);
        assertThat(transaction.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000000));
        assertThat(transaction.getStatus()).isEqualTo(RepaymentStatus.PENDING);
    }

    @Test
    @DisplayName("상환 상태 변경 테스트")
    void changeRepaymentStatus() {
        // given
        CreditTransaction transaction = CreditTransaction.builder()
                .type(TransactionType.LOAN)
                .amount(BigDecimal.valueOf(1000000))
                .status(RepaymentStatus.DELAYED)
                .build();

        // when
        transaction.changeStatus(RepaymentStatus.PAID);

        // then
        assertThat(transaction.getStatus()).isEqualTo(RepaymentStatus.PAID);
    }

    @Test
    @DisplayName("거래일자 조회 테스트")
    void getTransactionDate() {
        // given
        LocalDateTime now = LocalDateTime.now();
        CreditTransaction transaction = mock(CreditTransaction.class);
        when(transaction.getCreatedAt()).thenReturn(now);
        when(transaction.getTransactionDate()).thenCallRealMethod();

        // when
        LocalDateTime transactionDate = transaction.getTransactionDate();

        // then
        assertThat(transactionDate).isEqualTo(now);
        verify(transaction).getCreatedAt(); // createdAt이 호출되었는지 검증
    }

    @Test
    @DisplayName("상환 상태 변경 - 정상 케이스")
    void changeStatus_Success() {
        // given
        CreditTransaction transaction = CreditTransaction.builder()
                .type(TransactionType.LOAN)
                .amount(BigDecimal.valueOf(1000000))
                .status(RepaymentStatus.PENDING)
                .build();

        // when & then
        assertThatCode(() -> transaction.changeStatus(RepaymentStatus.PAID)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("상환 상태 변경 - null 값 검증")
    void changeStatus_NullValue() {
        // given
        CreditTransaction transaction = CreditTransaction.builder()
                .type(TransactionType.LOAN)
                .amount(BigDecimal.valueOf(1000000))
                .status(RepaymentStatus.PENDING)
                .build();

        // when & then
        assertThatThrownBy(() -> transaction.changeStatus(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상환 상태는 null일 수 없습니다.");
    }

    @Test
    @DisplayName("상환 상태 변경 - 동일 상태 변경 시도")
    void changeStatus_SameStatus() {
        // given
        CreditTransaction transaction = CreditTransaction.builder()
                .type(TransactionType.LOAN)
                .amount(BigDecimal.valueOf(1000000))
                .status(RepaymentStatus.PENDING)
                .build();

        // when & then
        assertThatThrownBy(() -> transaction.changeStatus(RepaymentStatus.PENDING))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("현재와 동일한 상태로 변경할 수 없습니다.");
    }

    @Test
    @DisplayName("상환 상태 변경 - 완료 상태에서 변경 시도")
    void changeStatus_FromPaid() {
        // given
        CreditTransaction transaction = CreditTransaction.builder()
                .type(TransactionType.LOAN)
                .amount(BigDecimal.valueOf(1000000))
                .status(RepaymentStatus.PAID)
                .build();

        // when & then
        assertThatThrownBy(() -> transaction.changeStatus(RepaymentStatus.PENDING))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("완료된 거래의 상태는 변경할 수 없습니다.");
    }

    @Test
    @DisplayName("상환 상태 변경 - 연체 상태에서 완료 외 상태로 변경 시도")
    void changeStatus_FromDelayed() {
        // given
        CreditTransaction transaction = CreditTransaction.builder()
                .type(TransactionType.LOAN)
                .amount(BigDecimal.valueOf(1000000))
                .status(RepaymentStatus.DELAYED)
                .build();

        // when & then
        assertThatThrownBy(() -> transaction.changeStatus(RepaymentStatus.PENDING))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("연체 상태에서는 완료 상태로만 변경할 수 있습니다.");
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
