package site.kimnow.toy.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import site.kimnow.toy.auth.application.AuthApplication;
import site.kimnow.toy.jwt.provider.JwtTokenProvider;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static site.kimnow.toy.common.constant.Constants.REFRESH_TOKEN;

@DisplayName("AuthController 테스트")
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AuthApplication authApplication;
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;
    private final String refreshToken = "mock-refresh-token";

    @Test
    @DisplayName("refreshToken 쿠키로 /reissue 호출 시 200 OK 반환")
    void reissue_success() throws Exception {
        mockMvc.perform(post("/api/v1/auth/reissue")
                        .cookie(new Cookie(REFRESH_TOKEN, refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("토큰 재발급 성공"));

        verify(authApplication).reissue(eq(refreshToken), any(HttpServletResponse.class));
    }

    @Test
    @DisplayName("refreshToekn 쿠키로 /logout 호출 시 200 OK 반환")
    void logout_success() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                        .cookie(new Cookie("refreshToken", refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("로그아웃 되었습니다."));

        verify(authApplication).logout(eq(refreshToken), any(HttpServletResponse.class));
    }
}
