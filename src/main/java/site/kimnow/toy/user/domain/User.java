package site.kimnow.toy.user.domain;

import lombok.*;
import site.kimnow.toy.common.util.RandomIdGenerator;
import site.kimnow.toy.user.entity.UserEntity;
import site.kimnow.toy.user.enums.UserAuthority;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    private String userId;
    private String email;
    private String name;
    private String password;
    private String authority;
    private boolean isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;

    public static User create(String email, String name, String password) {
        String userId = RandomIdGenerator.generate();
        return User.builder()
                .userId(userId)
                .email(email)
                .name(name)
                .password(password)
                .authority(UserAuthority.ROLE_USER.getInfo())
                .isDeleted(false)
                .build();
    }

    public static User fromEntity(UserEntity entity) {
        return User.builder()
                .userId(entity.getUserId())
                .email(entity.getEmail())
                .name(entity.getName())
                .password(entity.getPassword())
                .authority(entity.getAuthority())
                .isDeleted(entity.isDeleted())
                .createTime(entity.getCreateTime())
                .modifyTime(entity.getModifyTime())
                .build();
    }
}
