package kr.co.kcs.cims.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.kcs.cims.customer.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {}
