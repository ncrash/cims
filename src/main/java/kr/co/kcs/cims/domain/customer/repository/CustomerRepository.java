package kr.co.kcs.cims.domain.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.kcs.cims.domain.customer.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {}
