package site.kimnow.toy.user.command;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class JoinUserCommand {
    private final String email;
    private final String password;
    private final String name;
}
