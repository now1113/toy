package site.kimnow.toy.user.entity;

import jakarta.persistence.*;
import lombok.*;
import site.kimnow.toy.common.entity.BaseTimeEntity;


@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name= "users", indexes = {
        @Index(name = "ux_active_email", columnList = "active_email", unique = true)
    }
)
public class UserEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String email;
    @Column(
            name = "active_email",
            insertable = false,
            updatable = false,
            columnDefinition = "VARCHAR(255) GENERATED ALWAYS AS (CASE WHEN deleted = 0 THEN email ELSE NULL END) STORED"
    )
    private String activeEmail;
    private String name;
    private String password;
    private String authority;
    private String status;
    private boolean deleted;
}
