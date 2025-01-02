package kr.co.kcs.cims.domain.customer.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.kcs.cims.domain.customer.dto.CreditTransactionDto;
import kr.co.kcs.cims.domain.customer.dto.CreditTransactionRequestDto;
import kr.co.kcs.cims.domain.customer.dto.CustomerDto;
import kr.co.kcs.cims.domain.customer.entity.Customer;
import kr.co.kcs.cims.domain.customer.enums.RepaymentStatus;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerCreditFacade {
    private final CustomerService customerService;
    private final CreditTransactionService creditTransactionService;
    private final CreditScoreService creditScoreService;

    @Transactional
    public CreditTransactionDto createTransaction(CreditTransactionRequestDto creditRequest, Long customerId) {
        Customer customer = customerService.getCustomer(customerId);
        CreditTransactionDto transaction = creditTransactionService.createTransaction(customer, creditRequest);

        creditScoreService.updateCreditScore(customer.getId());

        return transaction;
    }

    @Transactional
    public CreditTransactionDto updateTransaction(Long customerId, Long transactionId, RepaymentStatus status) {
        verifyTransactionId(transactionId);

        Customer customer = customerService.getCustomer(customerId);
        CreditTransactionDto transaction = creditTransactionService.updateTransaction(customer, transactionId, status);

        creditScoreService.updateCreditScore(customer.getId());

        return transaction;
    }

    @Transactional
    public void deleteTransaction(Long customerId, Long transactionId) {
        verifyTransactionId(transactionId);

        Customer customer = customerService.getCustomer(customerId);
        creditTransactionService.deleteTransaction(customer, transactionId);

        creditScoreService.updateCreditScore(customer.getId());
    }

    public CustomerDto getCustomer(String username) {
        return CustomerDto.from(customerService.getCustomer(username));
    }

    private void verifyTransactionId(Long transactionId) {
        if (transactionId == null || transactionId < 0L) {
            throw new IllegalArgumentException("transactionId 파라미터가 잘못 되었습니다. " + transactionId);
        }
    }
}
