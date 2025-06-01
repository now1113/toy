package site.kimnow.toy.user.domain;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import site.kimnow.toy.common.util.RandomIdGenerator;
import site.kimnow.toy.user.enums.UserAuthority;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    private String userId;
    private String email;
    private String name;
    private String password;
    private String authority;
    private boolean deleted;
    private LocalDateTime createTime;
    private LocalDateTime modifyTime;

    /**
     * 암호화된 비밀번호로 새로운 유저 인스턴스를 반환
     */
    public User withEncodedPassword(String encodedPassword) {
        return User.builder()
                .userId(this.userId)
                .email(this.email)
                .name(this.name)
                .password(encodedPassword)
                .authority(this.authority)
                .deleted(this.deleted)
                .createTime(this.createTime)
                .modifyTime(this.modifyTime)
                .build();
    }

    public User encodePassword(PasswordEncoder encoder) {
        return this.withEncodedPassword(encoder.encode(this.password));
    }

    /**
     * 사용자 생성 규칙을 도메인에 숨긴 정적 팩토리 메서드
     */
    public static User create(String email, String name, String password) {
        return User.builder()
                .userId(RandomIdGenerator.generate())
                .email(email)
                .name(name)
                .password(password)
                .authority(UserAuthority.ROLE_USER.getInfo())
                .deleted(false)
                .createTime(LocalDateTime.now())
                .modifyTime(LocalDateTime.now())
                .build();
    }
}
