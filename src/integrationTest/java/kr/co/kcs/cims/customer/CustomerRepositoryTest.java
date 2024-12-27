package kr.co.kcs.cims.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import kr.co.kcs.cims.AbstractDataIntegrationTest;
import kr.co.kcs.cims.domain.customer.customer.Customer;
import kr.co.kcs.cims.domain.customer.customer.repository.CustomerRepository;

public class CustomerRepositoryTest extends AbstractDataIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(CustomerRepositoryTest.class);

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void test_findOneAvailableByMember() {
        // Given & When
        List<Customer> customers = customerRepository.findAll();

        // Then
        assertThat(customers).isEmpty();
    }
}
