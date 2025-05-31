package site.kimnow.toy.user.entity;

import jakarta.persistence.*;
import lombok.*;
import site.kimnow.toy.common.entity.BaseTimeEntity;


@Builder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name= "user", uniqueConstraints = {
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
    private boolean isDeleted;
}
