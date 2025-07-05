package site.kimnow.toy.user.event;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.kimnow.toy.user.domain.User;

@Getter
@Builder
@RequiredArgsConstructor
public class UserJoinedEvent {
    private final String email;

    public static UserJoinedEvent from(User user) {
        return UserJoinedEvent.builder()
                .email(user.getEmail())
                .build();
    }
}
