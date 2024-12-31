package kr.co.kcs.cims.controller.api.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.kcs.cims.config.TestSecurityConfig;
import kr.co.kcs.cims.infra.security.dto.LoginDto;
import kr.co.kcs.cims.infra.security.jwt.JwtTokenProvider;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccess() throws Exception {
        // Given
        String username = "testUser";
        String password = "testPassword";
        String token = "test.jwt.token";

        LoginDto loginDto = new LoginDto(username, password);
        UserDetails userDetails =
                new User(username, password, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        given(userDetailsService.loadUserByUsername(username)).willReturn(userDetails);
        given(jwtTokenProvider.createAccessToken(anyString(), any())).willReturn(token);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(token));

        verify(authenticationManager).authenticate(any());
        verify(userDetailsService).loadUserByUsername(username);
        verify(jwtTokenProvider).createAccessToken(anyString(), any());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 요청 형식")
    void loginFailure_InvalidRequest() throws Exception {
        // Given
        String invalidJson = "{\"username\": }"; // 잘못된 JSON 형식

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 실패 - 필수 필드 누락")
    void loginFailure_MissingRequiredFields() throws Exception {
        // Given
        String incompleteJson = "{}"; // 필수 필드 누락

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(incompleteJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
