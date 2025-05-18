package site.kimnow.toy.user.dto.request;

import lombok.*;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticatedUser {
    private String userId;
    private String role;
    public static AuthenticatedUser of(String userId, String role) {
        return AuthenticatedUser.builder()
                .userId(userId)
                .role(role)
                .build();
    }
}
