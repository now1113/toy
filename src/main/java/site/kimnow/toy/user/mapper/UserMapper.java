package site.kimnow.toy.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.entity.UserEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMapper {

    public static User toDomain(UserEntity entity) {
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

    public static UserEntity toEntity(User user) {
        return UserEntity.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .password(user.getPassword())
                .authority(user.getAuthority())
                .isDeleted(user.isDeleted())
                .build();
    }
}
