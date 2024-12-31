package kr.co.kcs.cims.domain.customer.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import kr.co.kcs.cims.domain.customer.entity.CreditTransaction;
import kr.co.kcs.cims.domain.customer.entity.Customer;
import kr.co.kcs.cims.domain.customer.enums.RepaymentStatus;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUsername(String username);

    @Query("SELECT ct FROM CreditTransaction ct WHERE ct.customer.id = :customerId AND ct.id = :transactionId")
    Optional<CreditTransaction> findTransactionByCustomerIdAndTransactionId(
            @Param("customerId") Long customerId, @Param("transactionId") Long transactionId);

    @Query("SELECT ct FROM CreditTransaction ct " + "WHERE ct.customer.id = :customerId "
            + "AND (:status IS NULL OR ct.status = :status)")
    Page<CreditTransaction> findTransactionsByCustomerId(
            @Param("customerId") Long customerId, @Param("status") RepaymentStatus status, Pageable pageable);

    @Query("SELECT ct FROM CreditTransaction ct WHERE ct.status = :status")
    Page<CreditTransaction> findTransactionsByStatus(@Param("status") RepaymentStatus status, Pageable pageable);

    @Query("SELECT COUNT(ct) FROM CreditTransaction ct " + "WHERE ct.customer = :customer "
            + "AND ct.createdAt >= :date "
            + "AND ct.status = :status")
    int countDelayedByCustomerAndDateAfter(
            @Param("customer") Customer customer,
            @Param("date") LocalDateTime date,
            @Param("status") RepaymentStatus status);

    @Query("SELECT COALESCE(SUM(ct.amount), 0) FROM CreditTransaction ct " + "WHERE ct.customer = :customer")
    BigDecimal sumTransactionAmountsByCustomer(@Param("customer") Customer customer);

    void deleteCreditTransactionById(Long transactionId);
}
