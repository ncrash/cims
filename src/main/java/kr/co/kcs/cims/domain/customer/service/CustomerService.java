package kr.co.kcs.cims.domain.customer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

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
        String encodedPassword = passwordEncoder.encode(request.password());

        Customer customer = request.toEntity(encodedPassword);
        Customer savedCustomer = customerRepository.save(customer);
        return CustomerDto.from(savedCustomer);
    }

    @Transactional
    public CustomerDto updateCustomer(String username, CustomerRequestDto request) {
        String encodedPassword = passwordEncoder.encode(request.password());

        Customer customer = getCustomer(username);

        if (!customer.getUsername().equals(request.username())) {
            throw new IllegalArgumentException(
                    "username은 변경할 수 없습니다. old: " + customer.getUsername() + ", new: " + request.username());
        }

        Customer updateRequestEntity = request.toEntity(encodedPassword);
        customer.updatePersonalInfo(updateRequestEntity.getPersonalInfo());

        return CustomerDto.from(customer);
    }

    @Transactional
    public void deleteCustomer(String username) {
        Customer customer = getCustomer(username);
        customerRepository.delete(customer);
    }

    Customer getCustomer(Long id) {
        return customerRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
    }

    Customer getCustomer(String username) {
        return customerRepository
                .findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with username: " + username));
    }
}
