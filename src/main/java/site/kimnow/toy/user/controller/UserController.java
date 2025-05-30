package site.kimnow.toy.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.kimnow.toy.common.response.CommonResponse;
import site.kimnow.toy.common.response.ResponseUtil;
import site.kimnow.toy.user.application.UserApplication;
import site.kimnow.toy.user.dto.request.LoginUser;
import site.kimnow.toy.user.dto.request.UserJoinRequest;
import site.kimnow.toy.user.dto.response.UserJoinResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserApplication userApplication;

    @GetMapping("/me")
    public void me(@AuthenticationPrincipal LoginUser user) {
        log.info("user id: {}, role: {}", user.getUserId(), user.getRole());
    }

    @PostMapping
    public ResponseEntity<CommonResponse<UserJoinResponse>> join(@RequestBody @Valid UserJoinRequest dto) {
        UserJoinResponse response = userApplication.join(dto);
        return ResponseUtil.ok(response);
    }

}
