package site.kimnow.toy.user.domain;

import lombok.*;
import site.kimnow.toy.common.util.RandomIdGenerator;
import site.kimnow.toy.user.enums.UserAuthority;
import site.kimnow.toy.user.enums.UserStatus;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    private Long idx;
    private String userId;
    private String email;
    private String name;
    private String password;
    private String authority;
    private String status;
    private boolean deleted;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;

    public User withEncodedPassword(String encodedPassword) {
        return User.builder()
                .userId(this.userId)
                .email(this.email)
                .name(this.name)
                .password(encodedPassword)
                .authority(this.authority)
                .status(this.status)
                .deleted(this.deleted)
                .createTime(this.createTime)
                .modifyTime(this.modifyTime)
                .build();
    }

    public static User create(String email, String name, String password) {
        return User.builder()
                .userId(RandomIdGenerator.generate())
                .email(email)
                .name(name)
                .password(password)
                .authority(UserAuthority.ROLE_USER.getInfo())
                .status(UserStatus.PENDING_EMAIL_VERIFICATION.getStatus())
                .deleted(false)
                .createTime(LocalDateTime.now())
                .modifyTime(LocalDateTime.now())
                .build();
    }

    public void completeEmailVerification() {
        this.status = UserStatus.EMAIL_VERIFIED.getStatus();
    }
}
