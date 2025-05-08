package site.kimnow.toy.user.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.kimnow.toy.common.response.CommonResponse;
import site.kimnow.toy.user.application.UserApplication;
import site.kimnow.toy.user.dto.request.UserJoinRequest;
import site.kimnow.toy.user.dto.response.UserJoinResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserEndpoint {

    private final UserApplication userApplication;

    @PostMapping("/v1/join")
    public ResponseEntity<CommonResponse<UserJoinResponse>> join(@RequestBody UserJoinRequest dto) {
        UserJoinResponse response = userApplication.join(dto);
        return CommonResponse.success(response);
    }
}
