package site.kimnow.toy.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import site.kimnow.toy.user.command.JoinUserCommand;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.dto.response.UserJoinResponse;
import site.kimnow.toy.user.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserApplication {

    private final UserService userService;

    public UserJoinResponse join(JoinUserCommand command) {
        User user = User.create(command.getEmail(), command.getName(), command.getPassword());
        userService.join(user);

        return UserJoinResponse.from(user);
    }
}
