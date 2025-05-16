package site.kimnow.toy.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.dto.request.UserJoinRequest;
import site.kimnow.toy.user.dto.response.UserJoinResponse;
import site.kimnow.toy.user.exception.DuplicateEmailException;
import site.kimnow.toy.user.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserApplication {

    private final UserService userService;

    public UserJoinResponse join(UserJoinRequest dto) {
        // 이메일 중복 체크
        if (userService.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException(dto.getEmail());
        }

        // 도메인 객체 생성
        User user = User.create(dto.getEmail(), dto.getName(), dto.getPassword());

        userService.join(user);

        return UserJoinResponse.from(user.getName());
    }
}
