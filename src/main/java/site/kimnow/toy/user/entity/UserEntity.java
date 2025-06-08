package site.kimnow.toy.user.entity;

import jakarta.persistence.*;
import lombok.*;
import site.kimnow.toy.common.entity.BaseTimeEntity;


@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name= "users", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_email", columnNames = "email")
})
public class UserEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    private String userId;
    private String email;
    private String name;
    private String password;
    private String authority;
    private boolean deleted;
}
