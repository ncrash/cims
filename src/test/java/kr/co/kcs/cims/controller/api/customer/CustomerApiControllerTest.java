package kr.co.kcs.cims.controller.api.customer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import kr.co.kcs.cims.config.TestSecurityConfig;
import kr.co.kcs.cims.domain.customer.dto.CustomerDto;
import kr.co.kcs.cims.domain.customer.dto.CustomerDtoBuilder;
import kr.co.kcs.cims.domain.customer.dto.CustomerDtoPersonalInfoDtoBuilder;
import kr.co.kcs.cims.domain.customer.dto.CustomerRequestDto;
import kr.co.kcs.cims.domain.customer.dto.CustomerRequestDtoBuilder;
import kr.co.kcs.cims.domain.customer.service.CustomerService;

@WebMvcTest(CustomerApiController.class)
@Import(TestSecurityConfig.class)
class CustomerApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    private CustomerDto sampleDto;
    private CustomerRequestDto sampleRequestDto;

    @BeforeEach
    void setUp() {
        CustomerDto.PersonalInfoDto personalInfoDto = CustomerDtoPersonalInfoDtoBuilder.builder()
                .name("홍길동")
                .email("hong@example.com")
                .phoneNumber("010-1234-5678")
                .build();
        sampleDto = CustomerDtoBuilder.builder()
                .id(1L)
                .personalInfo(personalInfoDto)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequestDto = CustomerRequestDtoBuilder.builder()
                .name("홍길동")
                .birthDate(LocalDate.of(1988, 9, 17))
                .email("hong@korea.com")
                .phoneNumber("010-1234-5678")
                .build();
    }

    @Test
    @DisplayName("고객 목록 조회 테스트")
    void getCustomersTest() throws Exception {
        Page<CustomerDto> page = new PageImpl<>(List.of(sampleDto));
        when(customerService.findCustomers(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/customers").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(sampleDto.id()))
                .andExpect(jsonPath("$.content[0].personalInfo.name")
                        .value(sampleDto.personalInfo().name()));
    }

    @Test
    @DisplayName("고객 상세 조회 테스트")
    void getCustomerTest() throws Exception {
        when(customerService.findCustomerId(eq(1L))).thenReturn(sampleDto);

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleDto.id()))
                .andExpect(jsonPath("$.personalInfo.name")
                        .value(sampleDto.personalInfo().name()))
                .andExpect(jsonPath("$.personalInfo.email")
                        .value(sampleDto.personalInfo().email()));
    }

    @Test
    @DisplayName("고객 등록 테스트")
    void createCustomerTest() throws Exception {
        when(customerService.createCustomer(any())).thenReturn(sampleDto);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleDto.id()));
    }

    @Test
    @DisplayName("고객 정보 수정 테스트")
    void updateCustomerTest() throws Exception {
        var updatedCustomer = CustomerDtoBuilder.builder()
                .id(1L)
                .personalInfo(CustomerDtoPersonalInfoDtoBuilder.builder()
                        .name("김신용")
                        .birthDate(LocalDate.of(1990, 1, 15))
                        .email("kim@test.com")
                        .phoneNumber("010-1234-5678")
                        .build())
                .build();

        when(customerService.updateCustomer(eq(1L), any())).thenReturn(updatedCustomer);

        mockMvc.perform(put("/api/v1/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.personalInfo.name").value("김신용"));
    }

    @Test
    @DisplayName("고객 삭제 테스트")
    void deleteCustomerTest() throws Exception {
        mockMvc.perform(delete("/api/v1/customers/1")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("고객 등록 실패 테스트 - 잘못된 요청")
    void createCustomerFailTest() throws Exception {
        when(customerService.createCustomer(any())).thenThrow(new IllegalArgumentException("잘못된 요청"));

        CustomerRequestDto badRequestDto = CustomerRequestDtoBuilder.builder().build();

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않는 고객 조회 테스트")
    void getNonExistentCustomerTest() throws Exception {
        when(customerService.findCustomerId(eq(999L))).thenThrow(new IllegalArgumentException("존재하지 않는 고객"));

        assertThatThrownBy(() -> mockMvc.perform(get("/api/v1/customers/999")).andExpect(status().isBadRequest()))
                .isInstanceOf(ServletException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 고객");
    }
}
