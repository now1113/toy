package site.kimnow.toy.user.domain;

import lombok.*;
import site.kimnow.toy.user.exception.UserVerificationExpiredException;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserVerification {

    private Long idx;
    private String email;
    private String token;
    private LocalDateTime expireDate;

    public static UserVerification of(String email, String token, LocalDateTime expireDate) {
        return UserVerification.builder()
                .email(email)
                .token(token)
                .expireDate(expireDate)
                .build();
    }

    public void validateNotExpired() {
        if (isExpired()) {
            throw new UserVerificationExpiredException();
        }
    }

    private boolean isExpired() {
        return expireDate.isBefore(LocalDateTime.now());
    }
}
