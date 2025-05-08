package site.kimnow.toy.user.domain;

import lombok.*;
import site.kimnow.toy.user.command.JoinUser;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    private String id;
    private String email;
    private String password;
    private String name;
    private String salt;

    public static User from(JoinUser command) {
        return User.builder()
                .id(command.getId())
                .email(command.getEmail())
                .password(command.getPassword())
                .name(command.getName())
                .salt(command.getSalt())
                .build();
    }
}
