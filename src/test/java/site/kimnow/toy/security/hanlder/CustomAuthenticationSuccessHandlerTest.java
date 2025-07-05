package site.kimnow.toy.security.hanlder;

import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import site.kimnow.toy.common.properties.JwtProperties;
import site.kimnow.toy.jwt.provider.JwtTokenProvider;
import site.kimnow.toy.redis.service.TokenRedisService;
import site.kimnow.toy.redis.service.UserRoleRedisService;
import site.kimnow.toy.security.handler.CustomAuthenticationSuccessHandler;
import site.kimnow.toy.security.vo.UserPrincipal;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static site.kimnow.toy.common.constant.Constants.ACCESS_TOKEN;
import static site.kimnow.toy.common.constant.Constants.REFRESH_TOKEN;
import static site.kimnow.toy.user.enums.UserAuthority.ROLE_USER;

@ExtendWith(MockitoExtension.class)
public class CustomAuthenticationSuccessHandlerTest {

    @InjectMocks
    private CustomAuthenticationSuccessHandler successHandler;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private JwtProperties jwtProperties;
    @Mock
    private TokenRedisService tokenRedisService;
    @Mock
    private UserRoleRedisService userRoleRedisService;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        UserPrincipal principal = mock(UserPrincipal.class);
        when(principal.getUsername()).thenReturn("user123");
        when(principal.getAuthority()).thenReturn(ROLE_USER.getInfo());

        authentication = new UsernamePasswordAuthenticationToken(principal, null);

        when(jwtTokenProvider.createAccessToken(anyString(), anyString())).thenReturn(ACCESS_TOKEN);
        when(jwtTokenProvider.createRefreshToken(anyString())).thenReturn(REFRESH_TOKEN);

        when(jwtProperties.getAccessTokenExpirationMills()).thenReturn(1000L * 60 * 10);
        when(jwtProperties.getRefreshTokenExpirationMills()).thenReturn(1000L * 60 * 60 * 24 * 14);
    }

    @Test
    @DisplayName("로그인 성공 시 토큰을 쿠키로 설정하고 Redis에 저장하며, 성공 응답을 반환한다")
    void onAuthenticationSuccess() throws Exception {
        // when
        successHandler.onAuthenticationSuccess(request, response, authentication);

        // then
        verify(jwtTokenProvider).createAccessToken("user123", ROLE_USER.getInfo());
        verify(jwtTokenProvider).createRefreshToken("user123");

        verify(tokenRedisService).save(eq("user123"),eq(REFRESH_TOKEN), any(Duration.class));
        verify(userRoleRedisService).save(eq("user123"), eq(ROLE_USER.getInfo()), any(Duration.class));


        List<String> setCookies = response.getHeaders("Set-Cookie");
        assertThat(setCookies.stream().anyMatch(cookie -> cookie.contains(ACCESS_TOKEN))).isTrue();
        assertThat(setCookies.stream().anyMatch(cookie -> cookie.contains(REFRESH_TOKEN))).isTrue();
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
        assertThat(response.getContentAsString()).contains("로그인에 성공했습니다.");
    }
}
