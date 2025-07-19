package site.kimnow.toy.user.domain;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import site.kimnow.toy.common.base.domain.AggregateRoot;
import site.kimnow.toy.common.util.RandomIdGenerator;
import site.kimnow.toy.user.enums.UserAuthority;
import site.kimnow.toy.user.enums.UserStatus;
import site.kimnow.toy.user.event.UserJoinedEvent;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends AggregateRoot<User, UserId> {

    private UserId id;
    private String userId;
    private String email;
    private String activeEmail;
    private String name;
    private String password;
    private String authority;
    private String status;
    private boolean deleted;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;

    @Override
    public UserId getId() {
        return id;
    }

    public static User create(String email, String name, String password) {
        User user = User.builder()
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

        user.registerEvent(UserJoinedEvent.from(user));
        return user;
    }

    public void encodePassword(PasswordEncoder encoder) {
        this.password = encoder.encode(password);
    }

    public void completeEmailVerification() {
        this.status = UserStatus.EMAIL_VERIFIED.getStatus();
    }
}
