package site.kimnow.toy.auth.endpoint;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.kimnow.toy.auth.application.AuthApplication;
import site.kimnow.toy.common.response.CommonResponse;

import static site.kimnow.toy.common.constant.Constants.REFRESH_TOKEN;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthEndpoint {

    private final AuthApplication authApplication;

    @PostMapping("/reissue")
    public ResponseEntity<CommonResponse<String>> reissue(
            @CookieValue(name = REFRESH_TOKEN, required = false) String refreshToken,
            HttpServletResponse response) {

        authApplication.reissue(refreshToken, response);
        return ResponseEntity.ok(CommonResponse.success("토큰 재발급 성공"));
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<String>> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        authApplication.logout(refreshToken, response);
        return ResponseEntity.ok(CommonResponse.success("로그아웃 되었습니다."));
    }
}
