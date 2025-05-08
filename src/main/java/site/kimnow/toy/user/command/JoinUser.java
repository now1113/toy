package site.kimnow.toy.user.command;

import lombok.*;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class JoinUser {
    private String id;
    private String email;
    private String password;
    private String name;
    private String salt;

    public static JoinUser of(String id, String email, String password, String name, String salt) {
        return JoinUser.builder()
                .id(id)
                .email(email)
                .password(password)
                .name(name)
                .salt(salt)
                .build();
    }
}
