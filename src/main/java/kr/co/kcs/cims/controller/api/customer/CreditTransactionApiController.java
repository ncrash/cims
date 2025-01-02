package kr.co.kcs.cims.controller.api.customer;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.kcs.cims.controller.api.common.PageResponseDto;
import kr.co.kcs.cims.domain.customer.dto.CreditTransactionDto;
import kr.co.kcs.cims.domain.customer.dto.CreditTransactionRequestDto;
import kr.co.kcs.cims.domain.customer.dto.CustomerDto;
import kr.co.kcs.cims.domain.customer.enums.RepaymentStatus;
import kr.co.kcs.cims.domain.customer.service.CreditTransactionService;
import kr.co.kcs.cims.domain.customer.service.CustomerCreditFacade;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(name = "CreditTransaction", description = "신용거래 관리 API")
public class CreditTransactionApiController {

    private final CreditTransactionService creditTransactionService;
    private final CustomerCreditFacade customerCreditFacade;

    @Operation(summary = "신용거래 전체 목록 조회", description = "페이징된 신용거래 전체 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<PageResponseDto<CreditTransactionDto>> getTransactions(
            @RequestParam(required = false) RepaymentStatus status, Pageable pageable) {
        Page<CreditTransactionDto> transactionsByStatus =
                creditTransactionService.getTransactionsByStatus(status, pageable);
        return ResponseEntity.ok(PageResponseDto.from(transactionsByStatus));
    }

    @Operation(summary = "고객 신용거래 목록 조회", description = "로그인한 고객의 신용거래 목록을 조회합니다.")
    @GetMapping("/customer")
    public ResponseEntity<PageResponseDto<CreditTransactionDto>> getCustomerTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) RepaymentStatus status,
            Pageable pageable) {
        CustomerDto customerDto = customerCreditFacade.getCustomer(userDetails.getUsername());
        Page<CreditTransactionDto> transactionsByCustomer = creditTransactionService.getTransactionsByCustomer(
                customerDto.id(), Optional.ofNullable(status), pageable);
        return ResponseEntity.ok(PageResponseDto.from(transactionsByCustomer));
    }

    @Operation(summary = "신용거래 등록", description = "새로운 신용거래를 등록합니다.")
    @PostMapping
    public ResponseEntity<CreditTransactionDto> createTransaction(
            @Valid @RequestBody CreditTransactionRequestDto creditRequest,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            CustomerDto customerDto = customerCreditFacade.getCustomer(userDetails.getUsername());
            CreditTransactionDto result = customerCreditFacade.createTransaction(creditRequest, customerDto.id());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("신용거래 등록 실패: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("신용거래 등록 중 오류가 발생했습니다", e);
        }
    }

    @Operation(summary = "신용거래 상태 수정", description = "신용거래의 상환 상태를 수정합니다.")
    @PatchMapping("/transaction/{transactionId}/status/{status}")
    public ResponseEntity<CreditTransactionDto> updateTransactionStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long transactionId,
            @Valid @PathVariable RepaymentStatus status) {
        try {
            CustomerDto customerDto = customerCreditFacade.getCustomer(userDetails.getUsername());
            CreditTransactionDto result =
                    customerCreditFacade.updateTransaction(customerDto.id(), transactionId, status);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("신용거래 상태 수정 실패: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("신용거래 상태 수정 중 오류가 발생했습니다", e);
        }
    }

    @Operation(summary = "신용거래 삭제", description = "신용거래 정보를 삭제합니다.")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteTransaction(
            @AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {
        try {
            CustomerDto customerDto = customerCreditFacade.getCustomer(userDetails.getUsername());
            customerCreditFacade.deleteTransaction(customerDto.id(), id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("신용거래 삭제 실패: " + e.getMessage());
        } catch (NullPointerException e) {
            throw new IllegalArgumentException("존재하지 않는 신용거래입니다: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("신용거래 삭제 중 오류가 발생했습니다", e);
        }
    }
}
