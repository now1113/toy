package site.kimnow.toy.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.kimnow.toy.auth.application.AuthApplication;
import site.kimnow.toy.auth.command.VerifyEmailCommand;
import site.kimnow.toy.common.properties.AuthRedirectUrlProperties;
import site.kimnow.toy.common.response.CommonResponse;

import java.net.URI;

import static site.kimnow.toy.common.constant.Constants.REFRESH_TOKEN;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthApplication authApplication;
    private final AuthRedirectUrlProperties authRedirectUrlProperties;

    @PostMapping("/reissue")
    public ResponseEntity<CommonResponse<String>> reissue(
            @CookieValue(name = REFRESH_TOKEN) String refreshToken, HttpServletResponse response) {

        authApplication.reissue(refreshToken, response);
        return ResponseEntity.ok(CommonResponse.success(null,"토큰 재발급 성공"));
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<String>> logout(
            @CookieValue(name = "refreshToken") String refreshToken, HttpServletResponse response
    ) {
        authApplication.logout(refreshToken, response);
        return ResponseEntity.ok(CommonResponse.success(null,"로그아웃 되었습니다."));
    }

    @GetMapping("/verify")
    public ResponseEntity<Void> verify(@RequestParam(name = "token") String token) {
        authApplication.verify(VerifyEmailCommand.from(token));

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(authRedirectUrlProperties.getSuccess()))
                .build();
    }
}
