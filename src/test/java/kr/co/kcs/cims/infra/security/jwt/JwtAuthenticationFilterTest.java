package kr.co.kcs.cims.infra.security.jwt;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Authentication authentication;

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtTokenProvider);
        SecurityContextHolder.clearContext();
    }

    @Test
    void validToken_ShouldAuthenticate() throws Exception {
        // Given
        String token = "valid.jwt.token";
        String bearerToken = "Bearer " + token;
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getAuthentication(token)).thenReturn(authentication);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenProvider).getAuthentication(token);
    }

    @Test
    void noToken_ShouldContinueChain() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void invalidToken_ShouldReturnUnauthorized() throws Exception {
        // Given
        String token = "invalid.jwt.token";
        String bearerToken = "Bearer " + token;
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtTokenProvider.validateToken(token)).thenReturn(false);

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void expiredToken_ShouldReturnUnauthorized() throws Exception {
        // Given
        String token = "expired.jwt.token";
        String bearerToken = "Bearer " + token;
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtTokenProvider.validateToken(anyString()))
                .thenThrow(new ExpiredJwtException(null, null, "Token expired"));

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void invalidSignature_ShouldReturnUnauthorized() throws Exception {
        // Given
        String token = "invalid.signature.token";
        String bearerToken = "Bearer " + token;
        when(request.getHeader("Authorization")).thenReturn(bearerToken);
        when(jwtTokenProvider.validateToken(anyString())).thenThrow(new SignatureException("Invalid signature"));

        // When
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
