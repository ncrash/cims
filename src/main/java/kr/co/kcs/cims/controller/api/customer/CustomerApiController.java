package kr.co.kcs.cims.controller.api.customer;

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
import kr.co.kcs.cims.domain.customer.dto.CustomerDto;
import kr.co.kcs.cims.domain.customer.dto.CustomerRequestDto;
import kr.co.kcs.cims.domain.customer.service.CustomerCreditFacade;
import kr.co.kcs.cims.domain.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "고객 관리 API")
public class CustomerApiController {

    private final CustomerService customerService;
    private final CustomerCreditFacade customerCreditFacade;

    @Operation(summary = "고객 목록 조회", description = "페이징된 고객 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<PageResponseDto<CustomerDto>> getCustomers(Pageable pageable) {
        Page<CustomerDto> customers = customerService.findCustomers(pageable);
        return ResponseEntity.ok(PageResponseDto.from(customers));
    }

    @Operation(summary = "고객 상세 조회", description = "고객 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.findCustomerId(id));
    }

    @Operation(summary = "고객 상세 조회", description = "로그인한 고객 상세 정보를 조회합니다.")
    @GetMapping("/detail")
    public ResponseEntity<CustomerDto> getCustomer(@AuthenticationPrincipal UserDetails userDetails) {
        CustomerDto customerDto = customerCreditFacade.getCustomer(userDetails.getUsername());

        return ResponseEntity.ok(customerService.findCustomerId(customerDto.id()));
    }

    @Operation(summary = "고객 등록", description = "새로운 고객을 등록합니다.")
    @PostMapping
    public ResponseEntity<CustomerDto> createCustomer(@Valid @RequestBody CustomerRequestDto request) {
        return ResponseEntity.ok(customerService.createCustomer(request));
    }

    @Operation(summary = "고객 정보 수정", description = "기존 고객의 정보를 수정합니다.")
    @PutMapping
    public ResponseEntity<CustomerDto> updateCustomer(
            @AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody CustomerRequestDto request) {
        return ResponseEntity.ok(customerService.updateCustomer(userDetails.getUsername(), request));
    }

    @Operation(summary = "고객 삭제", description = "고객 정보를 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<Void> deleteCustomer(@AuthenticationPrincipal UserDetails userDetails) {
        customerService.deleteCustomer(userDetails.getUsername());
        return ResponseEntity.ok().build();
    }
}
