package site.kimnow.toy.user.event;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.kimnow.toy.common.base.event.DomainEvent;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.domain.UserId;

@Getter
@Builder
@RequiredArgsConstructor
public class UserJoinedEvent implements DomainEvent {
    private final UserId userId;
    private final String email;

    public static UserJoinedEvent from(User user) {
        return UserJoinedEvent.builder()
                .email(user.getEmail())
                .build();
    }
}
