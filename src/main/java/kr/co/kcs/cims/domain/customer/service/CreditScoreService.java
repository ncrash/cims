package kr.co.kcs.cims.domain.customer.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import kr.co.kcs.cims.domain.customer.entity.Customer;
import kr.co.kcs.cims.domain.customer.enums.CreditGrade;
import kr.co.kcs.cims.domain.customer.enums.RepaymentStatus;
import kr.co.kcs.cims.domain.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditScoreService {

    // 신용등급 최대값 (10점)
    private static final int MAX_SCORE = 10;

    // 신용등급 기본값 (7점) - (연체 없고 정상 거래)
    private static final int BASE_SCORE = 7;

    // 신용등급 3회연체 시 강등값 (5점)
    private static final int MID_SCORE = 5;

    // 신용등급 최소값 (1점)
    private static final int MIN_SCORE = 1;

    // 1년 내 1회 연체: -2점
    private static final int DELAYED_PENALTY = -2;

    private final CustomerRepository customerRepository;

    @Transactional
    public void updateCreditScore(Long customerId) {
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));

        BigDecimal totalAmount = customerRepository.sumTransactionAmountsByCustomer(customer);
        int currentGrade = customer.getCreditGrade();
        int delayedCount = calculateDelayedCount(customer); // 연체 횟수에 따른 감점
        int bonusPoints = calculateBonusPoints(totalAmount); // 거래금액에 따른 가산점

        // 최종 등급은 1보다 작을 수 없음
        int newScore = calculateScore(totalAmount, currentGrade, delayedCount, bonusPoints);

        // 고객 신용점수 업데이트
        customer.updateCreditGrade(CreditGrade.findByGrade(newScore));
        customerRepository.save(customer);
    }

    private int calculateDelayedCount(Customer customer) {
        LocalDateTime oneYearAgo = LocalDateTime.now(customer.getClock()).minusYears(1);

        // 1년 내 연체 건수 조회
        return customerRepository.countDelayedByCustomerAndDateAfter(customer, oneYearAgo, RepaymentStatus.DELAYED);
    }

    private int calculateScore(BigDecimal totalAmount, int currentGrade, int delayedCount, int bonusPoints) {
        if (totalAmount.compareTo(BigDecimal.ZERO) > 0 && currentGrade == 0 && delayedCount == 0) {
            return BASE_SCORE;
        }

        // 3회 이상 연체: 5등급 이상은 5등급으로 강등하며, 5등급 이하인 경우에는 그대로 유지한다.
        if (delayedCount >= 3) {
            return Math.min(currentGrade, MID_SCORE);
        }

        // 1년내 1회 연체: -2점
        int deductPoints = 0;
        if (delayedCount == 1) {
            deductPoints += DELAYED_PENALTY;
        }

        return Math.min(MAX_SCORE, Math.max(MIN_SCORE, currentGrade + deductPoints + bonusPoints));
    }

    private int calculateBonusPoints(BigDecimal totalAmount) {

        // 거래금액에 따른 가산점 로직
        if (totalAmount.compareTo(BigDecimal.valueOf(100_000_000L)) > 0) { // 1억 이상
            return 2;
        } else if (totalAmount.compareTo(BigDecimal.valueOf(50_000_000L)) > 0) { // 5천만원 이상
            return 1;
        }
        return 0;
    }
}
