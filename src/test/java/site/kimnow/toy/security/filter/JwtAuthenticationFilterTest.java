package site.kimnow.toy.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import site.kimnow.toy.jwt.util.JwtTokenProvider;
import site.kimnow.toy.user.dto.request.LoginUser;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static site.kimnow.toy.common.constant.Constants.ACCESS_TOKEN;
import static site.kimnow.toy.user.enums.UserAuthority.ROLE_USER;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = Mockito.mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("doFilterInternal()")
    class Describe_doFilterInternal {

        @Test
        @DisplayName("유요한 토큰이 있을 경우 인증 정보를 설정한다")
        void withValidToken_setsAuthentication() throws Exception {
            // given
            String token = "valid.token";
            request.setCookies(new Cookie(ACCESS_TOKEN, token));
            when(jwtTokenProvider.validateToken(token)).thenReturn(true);
            when(jwtTokenProvider.getUserId(token)).thenReturn("user1");
            when(jwtTokenProvider.getRole(token)).thenReturn(ROLE_USER.getInfo());

            // when
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // then
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            assertThat(auth).isNotNull();
            assertThat(((LoginUser) auth.getPrincipal()).getUserId()).isEqualTo("user1");
        }

        @Test
        @DisplayName("토큰이 없으면 인증 없이 통과된다")
        void withoutToken_skipsAuthentication() throws Exception {
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }

        @Test
        @DisplayName("만료된 토큰이면 인증 없이 통과된다")
        void withExpiredToken_skipsAuthentication() throws Exception {
            // given
            String token = "expired";
            request.setCookies(new Cookie(ACCESS_TOKEN, token));
            when(jwtTokenProvider.validateToken(token)).thenThrow(new ExpiredJwtException(null, null, "Expired"));

            // when
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // then
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }

        @Test
        @DisplayName("유효하지 않은 토큰이면 인증 없이 통과된다")
        void withInvalidToken_skipsAuthentication() throws Exception {
            // given
            String token = "invalid";
            request.setCookies(new Cookie(ACCESS_TOKEN, token));
            when(jwtTokenProvider.validateToken(token)).thenReturn(false);

            // when
            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            // then
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }

    }
}
