package site.kimnow.toy.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import site.kimnow.toy.jwt.util.JwtTokenUtil;
import site.kimnow.toy.user.dto.request.AuthenticatedUser;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final String USER_SESSION = "userSession";


    /**
     *  JWT 토큰을 Cookie에서 추출하여 검증. 유효하면 request에 사용자 정보 설정
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = extractTokenFromCookie(request);

        if (token != null && jwtTokenUtil.validateToken(token)) {
            String userId = jwtTokenUtil.getUserId(token);
            String role = jwtTokenUtil.getRole(token);

            AuthenticatedUser authenticatedUser = AuthenticatedUser.of(userId, role);
            request.setAttribute(USER_SESSION, authenticatedUser);

            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
