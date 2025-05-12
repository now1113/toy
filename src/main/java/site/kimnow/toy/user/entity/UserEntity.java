package site.kimnow.toy.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import site.kimnow.toy.common.entity.BaseTimeEntity;
import site.kimnow.toy.user.domain.User;

import java.time.LocalDateTime;

@Entity(name = "user")
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserEntity extends BaseTimeEntity {

    @Id
    private String id;
    private String email;
    private String password;
    private String name;
    private String salt;

    public static UserEntity from(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .name(user.getName())
                .salt(user.getSalt())
                .build();
    }
}
