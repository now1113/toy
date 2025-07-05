package site.kimnow.toy.auth.application;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import site.kimnow.toy.auth.command.VerifyEmailCommand;
import site.kimnow.toy.auth.exception.AuthErrorCode;
import site.kimnow.toy.common.properties.JwtProperties;
import site.kimnow.toy.jwt.provider.JwtTokenProvider;
import site.kimnow.toy.redis.service.TokenRedisService;
import site.kimnow.toy.redis.service.UserRoleRedisService;
import site.kimnow.toy.user.domain.UserVerification;
import site.kimnow.toy.user.exception.UnauthorizedException;
import site.kimnow.toy.user.service.UserService;
import site.kimnow.toy.user.service.UserVerificationService;

import java.time.Duration;

import static site.kimnow.toy.auth.exception.AuthErrorCode.REFRESH_TOKEN_NOT_FOUND;
import static site.kimnow.toy.common.constant.Constants.ACCESS_TOKEN;
import static site.kimnow.toy.common.constant.Constants.REFRESH_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthApplication {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final TokenRedisService tokenRedisService;
    private final UserRoleRedisService userRoleRedisService;
    private final UserService userService;
    private final UserVerificationService userVerificationService;

    public void reissue(String refreshToken, HttpServletResponse response) {
        validateRefreshToken(refreshToken);

        String userId = getUserIdIfValid(refreshToken);
        String role = getUserRole(userId);

        rotateRefreshToken(refreshToken);
        generateAndAttachCookies(response, userId, role);
    }

    private String getUserIdIfValid(String refreshToken) {
        return tokenRedisService.get((refreshToken))
                .orElseThrow(() -> new UnauthorizedException(REFRESH_TOKEN_NOT_FOUND));
    }

    private void generateAndAttachCookies(HttpServletResponse response, String userId, String role) {
        String newAccessToken = jwtTokenProvider.createAccessToken(userId, role);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

        tokenRedisService.save(userId, newRefreshToken, Duration.ofDays(14));

        ResponseCookie accessCookie = ResponseCookie.from(ACCESS_TOKEN, newAccessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(jwtProperties.getAccessTokenExpirationMills() / 1000)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_TOKEN, newRefreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(jwtProperties.getRefreshTokenExpirationMills() / 1000)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private String getUserRole(String userId) {
        return userRoleRedisService.get(userId)
                .orElseThrow(UnauthorizedException::new);
    }

    private void rotateRefreshToken(String oldToken) {
        tokenRedisService.delete(oldToken);
    }

    public void logout(String refreshToken, HttpServletResponse response) {
        validateRefreshToken(refreshToken);

        String userId = jwtTokenProvider.getUserId(refreshToken);
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
        if (!StringUtils.hasText(refreshToken) || !jwtTokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException(AuthErrorCode.REFRESH_TOKEN_INVALID);
        }
    }

    public void verify(VerifyEmailCommand command) {
        UserVerification userVerification = userVerificationService.findByToken(command.getToken());
        userService.verify(userVerification);

        userVerificationService.delete(userVerification);
    }


}
