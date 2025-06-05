package site.kimnow.toy.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import site.kimnow.toy.common.response.CommonResponse;
import site.kimnow.toy.jwt.util.JwtProperties;
import site.kimnow.toy.jwt.util.JwtTokenProvider;
import site.kimnow.toy.redis.service.TokenRedisService;
import site.kimnow.toy.redis.service.UserRoleRedisService;
import site.kimnow.toy.security.vo.UserPrincipal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static site.kimnow.toy.common.constant.Constants.*;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final TokenRedisService tokenRedisService;
    private final UserRoleRedisService userRoleRedisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String userId = userPrincipal.getUsername();
        String authority = userPrincipal.getAuthority();

        String accessToken = jwtTokenProvider.createAccessToken(userId, authority);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);
        addTokens(accessToken, refreshToken, response);

        tokenRedisService.save(userId, refreshToken, Duration.ofDays(14));
        userRoleRedisService.save(userId, authority, Duration.ofDays(14));

        CommonResponse<String> successResponse = CommonResponse.success("로그인에 성공했습니다.");
        String json = new ObjectMapper().writeValueAsString(successResponse);

        response.getWriter().write(json);
    }

    private void addTokens(String accessToken, String refreshToken, HttpServletResponse response) {
        ResponseCookie access = ResponseCookie.from(ACCESS_TOKEN, accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite(DEFAULT_SAME_SITE)
                .path("/")
                .maxAge(jwtProperties.getAccessTokenExpirationMills() / 1000)
                .build();

        ResponseCookie refresh = ResponseCookie.from(REFRESH_TOKEN, refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite(DEFAULT_SAME_SITE)
                .path("/")
                .maxAge(jwtProperties.getRefreshTokenExpirationMills() / 1000)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, access.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refresh.toString());

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

    }
}
