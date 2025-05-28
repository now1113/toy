package site.kimnow.toy.auth.application;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import site.kimnow.toy.auth.exception.AuthErrorCode;
import site.kimnow.toy.jwt.util.JwtProperties;
import site.kimnow.toy.jwt.util.JwtTokenUtil;
import site.kimnow.toy.redis.service.TokenRedisService;
import site.kimnow.toy.redis.service.UserRoleRedisService;
import site.kimnow.toy.user.exception.UnauthorizedException;

import static site.kimnow.toy.auth.exception.AuthErrorCode.REFRESH_TOKEN_INVALID;
import static site.kimnow.toy.auth.exception.AuthErrorCode.REFRESH_TOKEN_NOT_FOUND;
import static site.kimnow.toy.common.constant.Constants.ACCESS_TOKEN;
import static site.kimnow.toy.common.constant.Constants.REFRESH_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthApplication {

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtProperties jwtProperties;
    private final TokenRedisService tokenRedisService;
    private final UserRoleRedisService userRoleRedisService;

    public void reissue(String refreshToken, HttpServletResponse response) {
        validateRefreshToken(refreshToken);
        String userId = jwtTokenUtil.getUserId(refreshToken);
        String role = userRoleRedisService.get(userId)
                .orElseThrow(UnauthorizedException::new);

        // Redis에서 저장된 refreshToken과 비교
        String savedToken = tokenRedisService.get(userId)
                .orElseThrow(() -> new UnauthorizedException(REFRESH_TOKEN_NOT_FOUND));

        if (!refreshToken.equals(savedToken)) {
            throw new UnauthorizedException(REFRESH_TOKEN_INVALID);
        }

        String newAccessToken = jwtTokenUtil.createAccessToken(userId, role);
        ResponseCookie accessCookie = ResponseCookie.from(ACCESS_TOKEN, newAccessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(jwtProperties.getAccessTokenExpirationMills() / 1000)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
    }

    public void logout(String refreshToken, HttpServletResponse response) {
        validateRefreshToken(refreshToken);

        String userId = jwtTokenUtil.getUserId(refreshToken);
        tokenRedisService.delete(userId);

        // 쿠키 만료
        expireCookie(ACCESS_TOKEN, response);
        expireCookie(REFRESH_TOKEN, response);
    }

    private void expireCookie(String name, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void validateRefreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken) || !jwtTokenUtil.validateToken(refreshToken)) {
            throw new UnauthorizedException(AuthErrorCode.REFRESH_TOKEN_INVALID);
        }
    }
}
