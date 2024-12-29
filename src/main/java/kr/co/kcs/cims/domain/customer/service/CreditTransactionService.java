package kr.co.kcs.cims.domain.customer.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import kr.co.kcs.cims.domain.customer.dto.CreditTransactionDto;
import kr.co.kcs.cims.domain.customer.dto.CreditTransactionRequestDto;
import kr.co.kcs.cims.domain.customer.entity.CreditTransaction;
import kr.co.kcs.cims.domain.customer.entity.Customer;
import kr.co.kcs.cims.domain.customer.enums.RepaymentStatus;
import kr.co.kcs.cims.domain.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreditTransactionService {
    private final CustomerRepository customerRepository;

    public Page<CreditTransactionDto> getTransactionsByStatus(RepaymentStatus status, Pageable pageable) {
        return customerRepository.findTransactionsByStatus(status, pageable).map(CreditTransactionDto::from);
    }

    public Page<CreditTransactionDto> getTransactionsByCustomer(
            Long customerId, Optional<RepaymentStatus> status, Pageable pageable) {
        return customerRepository
                .findTransactionsByCustomerId(customerId, status.orElse(null), pageable)
                .map(CreditTransactionDto::from);
    }

    @Transactional
    public CreditTransactionDto createTransaction(Customer customer, CreditTransactionRequestDto creditRequest) {
        CreditTransaction transaction = creditRequest.toEntity();

        customer.addTransaction(transaction);
        customerRepository.save(customer);

        return CreditTransactionDto.from(transaction);
    }

    @Transactional
    public CreditTransactionDto updateTransaction(Customer customer, Long transactionId, RepaymentStatus status) {
        CreditTransaction transaction = getCreditTransaction(customer, transactionId);

        transaction.changeStatus(status);
        customerRepository.save(customer);

        return CreditTransactionDto.from(transaction);
    }

    @Transactional
    public void deleteTransaction(Customer customer, Long transactionId) {
        CreditTransaction transaction = getCreditTransaction(customer, transactionId);

        try {
            customer.removeTransaction(transaction);
            customerRepository.deleteCreditTransactionById(transaction.getId());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("해당 거래내역을 찾을 수 없습니다: " + transactionId);
        } catch (Exception e) {
            throw new RuntimeException("거래 내역 삭제 중 예상치 못한 오류가 발생했습니다", e);
        }
    }

    private CreditTransaction getCreditTransaction(Customer customer, Long transactionId) {
        return customerRepository
                .findTransactionByCustomerIdAndTransactionId(customer.getId(), transactionId)
                .orElseThrow(() -> new EntityNotFoundException("CreditTransaction not found"));
    }
}
// TODO deleteTransaction 메소드는 정상 동작 확인함, createTransaction, updateTransaction 메소드 정상동작 테스트 해야함
