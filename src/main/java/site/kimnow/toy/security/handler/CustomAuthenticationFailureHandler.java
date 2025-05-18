package site.kimnow.toy.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import site.kimnow.toy.common.response.CommonResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        log.warn("인증 실패: {}", exception.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        CommonResponse<Object> fail = CommonResponse.fail(exception.getMessage(), HttpStatus.UNAUTHORIZED);
        String json = new ObjectMapper().writeValueAsString(fail);

        response.getWriter().write(json);
    }
}
