package site.kimnow.toy.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.dto.request.UserJoinRequest;
import site.kimnow.toy.user.dto.response.UserJoinResponse;
import site.kimnow.toy.user.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserApplication {

    private final UserService userService;

    public UserJoinResponse join(UserJoinRequest dto) {
        User user = User.create(dto.getEmail(), dto.getName(), dto.getPassword());
        userService.join(user);

        return UserJoinResponse.from(user);
    }
}
