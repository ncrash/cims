package kr.co.kcs.cims.controller.api.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Hidden
@RestController
@Tag(name = "Hello", description = "Hello World API")
public class HelloApiController {
    @Operation(summary = "Hello World 메시지 조회", description = "기본적인 Hello World 메시지를 반환합니다.")
    @GetMapping("/api/hello")
    public String hello() {
        return "Hello, World!";
    }
}
