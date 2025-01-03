package kr.co.kcs.cims.domain.customer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityNotFoundException;
import kr.co.kcs.cims.domain.customer.entity.Customer;
import kr.co.kcs.cims.domain.customer.entity.PersonalInfo;
import kr.co.kcs.cims.domain.customer.enums.CreditGrade;
import kr.co.kcs.cims.domain.customer.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CreditScoreServiceTest {

    @InjectMocks
    private CreditScoreService creditScoreService;

    @Mock
    private CustomerRepository customerRepository;

    @Test
    @DisplayName("신용등급이 없고 연체도 없고 모든 거래가 정상적으로 완료된 경우 기본 점수(7점)를 받는다")
    void updateCreditScore_WithBasicGrade() {
        // given
        Customer customer = createCustomer(1L, null);
        given(customerRepository.findById(1L)).willReturn(Optional.of(customer));
        given(customerRepository.countDelayedByCustomerAndDateAfter(any(), any(), any()))
                .willReturn(0);
        given(customerRepository.sumTransactionAmountsByCustomer(any())).willReturn(BigDecimal.TEN);

        // when
        creditScoreService.updateCreditScore(1L);

        // then
        assertThat(customer.getCreditGrade()).isEqualTo(7);
        verify(customerRepository).save(customer);
    }

    @Test
    @DisplayName("연체가 없는 경우 기본 점수(7점)를 받는다")
    void updateCreditScore_WithNoDelayed() {
        // given
        Customer customer = createCustomer(1L);
        given(customerRepository.findById(1L)).willReturn(Optional.of(customer));
        given(customerRepository.countDelayedByCustomerAndDateAfter(any(), any(), any()))
                .willReturn(0);
        given(customerRepository.sumTransactionAmountsByCustomer(any())).willReturn(BigDecimal.ZERO);

        // when
        creditScoreService.updateCreditScore(1L);

        // then
        assertThat(customer.getCreditGrade()).isEqualTo(7);
        verify(customerRepository).save(customer);
    }

    @Test
    @DisplayName("1년 내 1회 연체 시 2점 감점되어 10점 -> 8점이 된다")
    void updateCreditScore_WithOneDelayed() {
        // given
        Customer customer = createCustomer(1L, CreditGrade.GRADE_010);
        given(customerRepository.findById(1L)).willReturn(Optional.of(customer));
        given(customerRepository.countDelayedByCustomerAndDateAfter(any(), any(), any()))
                .willReturn(1);
        given(customerRepository.sumTransactionAmountsByCustomer(any())).willReturn(BigDecimal.ZERO);

        // when
        creditScoreService.updateCreditScore(1L);

        // then
        assertThat(customer.getCreditGrade()).isEqualTo(8);
        verify(customerRepository).save(customer);
    }

    @Test
    @DisplayName("3회 이상 연체 시 5점 이하가 된다")
    void updateCreditScore_WithThreeOrMoreDelayeds() {
        // given
        Customer customer = createCustomer(1L);
        customer.updateCreditGrade(CreditGrade.GRADE_009);
        given(customerRepository.findById(1L)).willReturn(Optional.of(customer));
        given(customerRepository.countDelayedByCustomerAndDateAfter(any(), any(), any()))
                .willReturn(3);
        given(customerRepository.sumTransactionAmountsByCustomer(any())).willReturn(BigDecimal.ZERO);

        // when
        creditScoreService.updateCreditScore(1L);

        // then
        assertThat(customer.getCreditGrade()).isLessThanOrEqualTo(5);
        verify(customerRepository).save(customer);
    }

    @Test
    @DisplayName("거래금액이 1억 이상인 경우 2점의 가산점을 받는다")
    void updateCreditScore_WithHighTransactionAmount() {
        // given
        Customer customer = createCustomer(1L);
        given(customerRepository.findById(1L)).willReturn(Optional.of(customer));
        given(customerRepository.countDelayedByCustomerAndDateAfter(any(), any(), any()))
                .willReturn(0);
        given(customerRepository.sumTransactionAmountsByCustomer(any())).willReturn(new BigDecimal("100000001"));

        // when
        creditScoreService.updateCreditScore(1L);

        // then
        assertThat(customer.getCreditGrade()).isEqualTo(9); // 7 + 2 = 9점
        verify(customerRepository).save(customer);
    }

    @Test
    @DisplayName("거래금액이 5천만원 이상 1억 미만인 경우 1점의 가산점을 받는다")
    void updateCreditScore_WithMediumTransactionAmount() {
        // given
        Customer customer = createCustomer(1L);
        given(customerRepository.findById(1L)).willReturn(Optional.of(customer));
        given(customerRepository.countDelayedByCustomerAndDateAfter(any(), any(), any()))
                .willReturn(0);
        given(customerRepository.sumTransactionAmountsByCustomer(any())).willReturn(new BigDecimal("50000001"));

        // when
        creditScoreService.updateCreditScore(1L);

        // then
        assertThat(customer.getCreditGrade()).isEqualTo(8); // 7 + 1 = 8점
        verify(customerRepository).save(customer);
    }

    @Test
    @DisplayName("존재하지 않는 고객 ID로 조회시 예외가 발생한다")
    void updateCreditScore_WithInvalidCustomerId() {
        // given
        given(customerRepository.findById(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> creditScoreService.updateCreditScore(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Customer not found with id: 999");
    }

    @Test
    @DisplayName("신용점수는 최소 1점 이하로 내려가지 않는다")
    void updateCreditScore_MinimumScoreTest() {
        // given
        Customer customer = createCustomer(1L, CreditGrade.GRADE_001);
        given(customerRepository.findById(1L)).willReturn(Optional.of(customer));
        given(customerRepository.countDelayedByCustomerAndDateAfter(any(), any(), any()))
                .willReturn(5); // 많은 연체 횟수
        given(customerRepository.sumTransactionAmountsByCustomer(any())).willReturn(BigDecimal.ZERO);

        // when
        creditScoreService.updateCreditScore(1L);

        // then
        assertThat(customer.getCreditGrade()).isEqualTo(1);
    }

    @Test
    @DisplayName("신용점수는 최대 10점을 초과하지 않는다")
    void updateCreditScore_MaximumScoreTest() {
        // given
        Customer customer = createCustomer(1L, CreditGrade.GRADE_010);
        given(customerRepository.findById(1L)).willReturn(Optional.of(customer));
        given(customerRepository.countDelayedByCustomerAndDateAfter(any(), any(), any()))
                .willReturn(0);
        given(customerRepository.sumTransactionAmountsByCustomer(any()))
                .willReturn(new BigDecimal("200000000")); // 매우 큰 거래금액

        // when
        creditScoreService.updateCreditScore(1L);

        // then
        assertThat(customer.getCreditGrade()).isEqualTo(10);
    }

    private Customer createCustomer(Long id) {
        return createCustomer(id, CreditGrade.GRADE_007);
    }

    private Customer createCustomer(Long id, CreditGrade initialGrade) {
        PersonalInfo personalInfo = PersonalInfo.builder().name("Test Customer").build();
        return Customer.builder()
                .id(id)
                .personalInfo(personalInfo)
                .creditGrade(initialGrade)
                .build();
    }
}
