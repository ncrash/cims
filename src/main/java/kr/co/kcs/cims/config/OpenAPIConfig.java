package kr.co.kcs.cims.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CIMS API")
                        .version("1.0")
                        .description("고객 신용 정보 관리 시스템 (Customer Information Management System) OpenAPI"));
    }
}
