package kr.co.kcs.cims.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Swagger UI 관련 경로
        String[] swaggerPaths = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/kcs/swagger.html",
            "/kcs/swagger-ui/**",
        };

        // 공개 API 경로
        String[] publicApiPaths = {"/api/hello", "/hello"};

        http.authorizeHttpRequests(authorize -> authorize
                        // Swagger UI 경로 허용
                        .requestMatchers(swaggerPaths)
                        .permitAll()
                        // 공개 API 경로 허용
                        .requestMatchers(publicApiPaths)
                        .permitAll()
                        // 나머지 요청은 인증 필요
                        .anyRequest()
                        .authenticated())
                .formLogin(withDefaults());

        return http.build();
    }
}
