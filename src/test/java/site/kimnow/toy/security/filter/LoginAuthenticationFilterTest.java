package site.kimnow.toy.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import site.kimnow.toy.user.dto.request.LoginRequest;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginAuthenticationFilterTest {

    @Mock
    private AuthenticationManager authenticationManager;
    private LoginAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        filter = new LoginAuthenticationFilter(authenticationManager);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("POST 요청 + 유효한 JSON -> authenticate 호출")
    void attemptAuthentication_success() throws Exception {
        // given
        request.setMethod("POST");
        request.setContentType("application/json");

        LoginRequest loginRequest = LoginRequest.of("test@example.com", "password123");
        byte[] requestBody = new ObjectMapper().writeValueAsBytes(loginRequest);
        request.setContent(requestBody);

        Authentication expectedAuth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(expectedAuth);

        // when
        Authentication result = filter.attemptAuthentication(request, response);

        // then
        assertThat(result).isEqualTo(expectedAuth);
        verify(authenticationManager).authenticate(argThat(auth->
                auth instanceof UsernamePasswordAuthenticationToken &&
                        auth.getPrincipal().equals(loginRequest.getEmail()) &&
                        auth.getCredentials().equals(loginRequest.getPassword())
                ));
    }

    @Test
    @DisplayName("POST가 아닌 요청이면 예외 발생")
    void attemptAuthentication_invalidMethod() {
        // given
        request.setMethod("GET");

        // expect
        assertThrows(AuthenticationServiceException.class, () ->
            filter.attemptAuthentication(request, response));
    }

    @Test
    @DisplayName("JSON 파싱 실패 시 예외가 발생한다")
    void attemptAuthentication_invalidJson() {
        // given
        request.setMethod("POST");
        request.setContentType("application/json");
        request.setContent("invalid json".getBytes(StandardCharsets.UTF_8));

        // expect
        assertThatThrownBy(() -> filter.attemptAuthentication(request, response))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("로그인 요청 JSON 파싱 실패");
    }

}
