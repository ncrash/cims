package kr.co.kcs.cims.infra.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    private final String TEST_SECRET = "testsecretkeytestsecretkeytestsecretkeytestsecretkey";
    private final String TEST_USERNAME = "testuser";
    private final List<String> TEST_ROLES = List.of("ROLE_USER");

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", TEST_SECRET);
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenValidityInSeconds", 3600L);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenValidityInSeconds", 86400L);
        jwtTokenProvider.init();
    }

    @Test
    @DisplayName("액세스 토큰 생성 테스트")
    void createAccessToken_ShouldGenerateValidToken() {
        // when
        String token = jwtTokenProvider.createAccessToken(TEST_USERNAME, TEST_ROLES);

        // then
        assertThat(token).isNotNull();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUserPk(token)).isEqualTo(TEST_USERNAME);
    }

    @Test
    @DisplayName("리프레시 토큰 생성 테스트")
    void createRefreshToken_ShouldGenerateValidToken() {
        // when
        String token = jwtTokenProvider.createRefreshToken(TEST_USERNAME, TEST_ROLES);

        // then
        assertThat(token).isNotNull();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUserPk(token)).isEqualTo(TEST_USERNAME);
    }

    @Test
    @DisplayName("토큰으로부터 인증 정보 추출 테스트")
    void getAuthentication_ShouldReturnValidAuthentication() {
        // given
        UserDetails userDetails = new User(
                TEST_USERNAME,
                "",
                TEST_ROLES.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        given(userDetailsService.loadUserByUsername(TEST_USERNAME)).willReturn(userDetails);

        String token = jwtTokenProvider.createAccessToken(TEST_USERNAME, TEST_ROLES);

        // when
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        // then
        assertThat(authentication).isNotNull();
        assertThat(authentication.getName()).isEqualTo(TEST_USERNAME);
        assertThat(authentication.getAuthorities()).extracting("authority").containsExactlyElementsOf(TEST_ROLES);
    }

    @Test
    @DisplayName("잘못된 토큰 검증 테스트")
    void validateToken_ShouldReturnFalseForInvalidToken() {
        // given
        String invalidToken = "invalidtoken";

        // when
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰 검증 테스트")
    void validateToken_ShouldReturnFalseForExpiredToken() {
        // given
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenValidityInSeconds", -3600L);
        String expiredToken = jwtTokenProvider.createAccessToken(TEST_USERNAME, TEST_ROLES);

        // when
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);

        // then
        assertThat(isValid).isFalse();
    }
}
