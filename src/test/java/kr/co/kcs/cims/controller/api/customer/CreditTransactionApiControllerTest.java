package kr.co.kcs.cims.controller.api.customer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
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
import kr.co.kcs.cims.domain.customer.dto.CreditTransactionDto;
import kr.co.kcs.cims.domain.customer.dto.CreditTransactionDtoBuilder;
import kr.co.kcs.cims.domain.customer.dto.CreditTransactionRequestDto;
import kr.co.kcs.cims.domain.customer.dto.CreditTransactionRequestDtoBuilder;
import kr.co.kcs.cims.domain.customer.enums.RepaymentStatus;
import kr.co.kcs.cims.domain.customer.enums.TransactionType;
import kr.co.kcs.cims.domain.customer.service.CreditTransactionService;
import kr.co.kcs.cims.domain.customer.service.CustomerCreditFacade;

@WebMvcTest(CreditTransactionApiController.class)
@Import(TestSecurityConfig.class)
class CreditTransactionApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CreditTransactionService creditTransactionService;

    @MockitoBean
    private CustomerCreditFacade customerCreditFacade;

    private CreditTransactionDto sampleDto;
    private CreditTransactionRequestDto sampleRequestDto;

    @BeforeEach
    void setUp() {
        sampleDto = CreditTransactionDtoBuilder.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(10000))
                .type(TransactionType.CREDIT_CARD)
                .status(RepaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleRequestDto = CreditTransactionRequestDtoBuilder.builder()
                .customerId(1L)
                .type(TransactionType.CREDIT_CARD)
                .amount(BigDecimal.valueOf(10000))
                .status(RepaymentStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("신용거래 목록 조회 테스트")
    void getTransactionsTest() throws Exception {
        Page<CreditTransactionDto> page = new PageImpl<>(List.of(sampleDto));
        when(creditTransactionService.getTransactionsByStatus(any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/transactions").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(sampleDto.id()));
    }

    @Test
    @DisplayName("고객별 신용거래 목록 조회 테스트")
    void getCustomerTransactionsTest() throws Exception {
        Page<CreditTransactionDto> page = new PageImpl<>(List.of(sampleDto));
        when(creditTransactionService.getTransactionsByCustomer(eq(1L), any(), any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/transactions/customer/1")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].type").value(TransactionType.CREDIT_CARD.name()))
                .andExpect(jsonPath("$.content[0].status").value(RepaymentStatus.PENDING.name()));
    }

    @Test
    @DisplayName("신용거래 등록 테스트")
    void createTransactionTest() throws Exception {
        when(customerCreditFacade.createTransaction(any())).thenReturn(sampleDto);

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sampleDto.id()));
    }

    @Test
    @DisplayName("신용거래 상태 수정 테스트")
    void updateTransactionStatusTest() throws Exception {
        var updateSampleDto = sampleDto.withStatus(RepaymentStatus.PAID);
        when(customerCreditFacade.updateTransaction(eq(1L), eq(1L), eq(RepaymentStatus.PAID)))
                .thenReturn(updateSampleDto);

        mockMvc.perform(patch("/api/v1/transactions/customer/1/transaction/1/status/PAID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(RepaymentStatus.PAID.name()));
    }

    @Test
    @DisplayName("신용거래 삭제 테스트")
    void deleteTransactionTest() throws Exception {
        mockMvc.perform(delete("/api/v1/transactions/customer/1/transaction/1")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("신용거래 등록 실패 테스트 - 잘못된 요청")
    void createTransactionFailTest() throws Exception {
        when(customerCreditFacade.createTransaction(any())).thenThrow(new IllegalArgumentException("잘못된 요청"));

        CreditTransactionRequestDto badRequestDto =
                CreditTransactionRequestDtoBuilder.builder().build();

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않는 거래 삭제 시도 테스트")
    void deleteNonExistentTransactionTest() throws Exception {
        doThrow(new IllegalArgumentException("존재하지 않는 거래"))
                .when(customerCreditFacade)
                .deleteTransaction(eq(1L), eq(999L));

        assertThatThrownBy(() -> mockMvc.perform(delete("/api/v1/transactions/customer/1/transaction/999"))
                        .andExpect(status().isBadRequest()))
                .isInstanceOf(ServletException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("신용거래 삭제 실패: 존재하지 않는 거래");
    }
}
