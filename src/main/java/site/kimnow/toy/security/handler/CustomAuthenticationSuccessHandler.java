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
import site.kimnow.toy.jwt.util.JwtTokenUtil;
import site.kimnow.toy.security.vo.UserPrincipal;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;
    private final JwtProperties jwtProperties;
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String DEFAULT_SAME_SITE = "Strict";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String userId = userPrincipal.getUsername();
        String authority = userPrincipal.getAuthority();

        String accessToken = jwtTokenUtil.createAccessToken(userId, authority);
        String refreshToken = jwtTokenUtil.createRefreshToken(userId, authority);

        ResponseCookie cookie = ResponseCookie.from(ACCESS_TOKEN, accessToken)
                .httpOnly(true)
                .secure(true)
                .sameSite(DEFAULT_SAME_SITE)
                .path("/")
                .maxAge(jwtProperties.getAccessTokenExpirationSeconds() / 1000)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        //TODO Refresh는 Redis에 저장

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        CommonResponse<String> successResponse = CommonResponse.success("로그인에 성공했습니다.");
        String json = new ObjectMapper().writeValueAsString(successResponse);

        response.getWriter().write(json);
    }
}
