package kr.co.kcs.cims.domain.customer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.kcs.cims.domain.customer.dto.CreditTransactionDto;
import kr.co.kcs.cims.domain.customer.dto.CreditTransactionRequestDto;
import kr.co.kcs.cims.domain.customer.entity.Customer;
import kr.co.kcs.cims.domain.customer.enums.RepaymentStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerCreditFacade {
    private final CustomerService customerService;
    private final CreditTransactionService creditTransactionService;

    @Transactional
    public CreditTransactionDto createTransaction(CreditTransactionRequestDto creditRequest) {
        Customer customer = customerService.getCustomer(creditRequest.customerId());
        CreditTransactionDto transaction = creditTransactionService.createTransaction(customer, creditRequest);

        // TODO 신용등급 재계산 이벤트 발행처리
        //        creditScoreService.updateCreditScore(customer.getId());

        return transaction;
    }

    public CreditTransactionDto updateTransaction(Long customerId, Long transactionId, RepaymentStatus status) {
        // TODO transaction id 필수값 체크

        Customer customer = customerService.getCustomer(customerId);
        CreditTransactionDto transaction = creditTransactionService.updateTransaction(customer, transactionId, status);

        // TODO 신용등급 재계산 이벤트 발행처리
        // 거래 수정 후 신용점수 갱신
        //        creditScoreService.updateCreditScore(transaction.getCustomer().getId());

        return transaction;
    }

    public void deleteTransaction(Long customerId, Long transactionId) {
        // TODO transaction id 필수값 체크

        Customer customer = customerService.getCustomer(customerId);
        creditTransactionService.deleteTransaction(customer, transactionId);

        // TODO 신용등급 재계산 이벤트 발행처리
        // 거래 수정 후 신용점수 갱신
        //        creditScoreService.updateCreditScore(transaction.getCustomer().getId());
    }
}
