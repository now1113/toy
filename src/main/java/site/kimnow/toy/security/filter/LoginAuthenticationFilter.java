package site.kimnow.toy.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import site.kimnow.toy.user.dto.request.LoginRequestDto;

import java.io.IOException;

public class LoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper om = new ObjectMapper();
    private boolean postOnly = true;
    private static final String LOGIN_URL = "/api/v1/user/login";

    public LoginAuthenticationFilter(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
        setFilterProcessesUrl(LOGIN_URL);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (this.postOnly && !"POST".equals(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        try {
            LoginRequestDto loginRequest = om.readValue(request.getInputStream(), LoginRequestDto.class);
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
            return this.getAuthenticationManager().authenticate(authRequest);

        } catch (IOException e) {
            throw new AuthenticationServiceException("로그인 요청 JSON 파싱 실패");
        }
    }

}
