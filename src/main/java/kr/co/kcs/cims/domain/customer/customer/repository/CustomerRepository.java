package kr.co.kcs.cims.domain.customer.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.kcs.cims.domain.customer.customer.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {}
