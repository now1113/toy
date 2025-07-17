package site.kimnow.toy.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import site.kimnow.toy.common.response.PageResponse;
import site.kimnow.toy.user.command.JoinUserCommand;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.dto.response.UserJoinResponse;
import site.kimnow.toy.user.dto.response.UserResponse;
import site.kimnow.toy.user.event.UserJoinedEvent;
import site.kimnow.toy.user.query.UserSearchQuery;
import site.kimnow.toy.user.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserApplication {

    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserJoinResponse join(JoinUserCommand command) {
        User user = User.create(command.getEmail(), command.getName(), command.getPassword());
        userService.join(user);

        eventPublisher.publishEvent(UserJoinedEvent.from(user));

        return UserJoinResponse.from(user);
    }

    public PageResponse<UserResponse> queryUsers(UserSearchQuery query) {
        userService.queryUsers(query);

        return null;
    }
}
