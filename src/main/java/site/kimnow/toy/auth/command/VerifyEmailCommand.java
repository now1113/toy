package site.kimnow.toy.auth.command;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class VerifyEmailCommand {
    private final String token;

    public static VerifyEmailCommand from(String token) {
        return VerifyEmailCommand.builder()
                .token(token)
                .build();
    }
}
