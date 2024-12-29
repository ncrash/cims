package kr.co.kcs.cims.domain.customer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import kr.co.kcs.cims.domain.customer.dto.CustomerDto;
import kr.co.kcs.cims.domain.customer.dto.CustomerRequestDto;
import kr.co.kcs.cims.domain.customer.entity.Customer;
import kr.co.kcs.cims.domain.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Page<CustomerDto> findCustomers(Pageable pageable) {
        Page<Customer> customers = customerRepository.findAll(pageable);
        return customers.map(CustomerDto::from);
    }

    public CustomerDto findCustomerId(Long id) {
        Customer customer = getCustomer(id);
        return CustomerDto.from(customer);
    }

    @Transactional
    public CustomerDto createCustomer(CustomerRequestDto request) {
        Customer customer = request.toEntity();
        Customer savedCustomer = customerRepository.save(customer);
        return CustomerDto.from(savedCustomer);
    }

    @Transactional
    public CustomerDto updateCustomer(Long id, CustomerRequestDto request) {
        Customer customer = getCustomer(id);
        Customer updateRequestEntity = request.toEntity();

        customer.updatePersonalInfo(updateRequestEntity.getPersonalInfo());

        return CustomerDto.from(customer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = getCustomer(id);
        customerRepository.delete(customer);
    }

    Customer getCustomer(Long id) {
        return customerRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
    }
}
