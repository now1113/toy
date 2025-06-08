package site.kimnow.toy.user.dto.request;

import lombok.*;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginUser {
    private String userId;
    private String role;
    public static LoginUser of(String userId, String role) {
        return LoginUser.builder()
                .userId(userId)
                .role(role)
                .build();
    }
}
