package site.kimnow.toy.user.mapper;

import org.springframework.stereotype.Component;
import site.kimnow.toy.common.base.mapper.DomainEntityMapper;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.domain.UserId;
import site.kimnow.toy.user.entity.UserEntity;

@Component
public class UserMapper implements DomainEntityMapper<User, UserEntity> {
    @Override
    public User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId() != null ? new UserId(entity.getId()) : null)
                .userId(entity.getUserId())
                .email(entity.getEmail())
                .activeEmail(entity.getActiveEmail())
                .name(entity.getName())
                .password(entity.getPassword())
                .authority(entity.getAuthority())
                .status(entity.getStatus())
                .deleted(entity.isDeleted())
                .build();
    }

    @Override
    public UserEntity toEntity(User domain) {
        return UserEntity.builder()
                .id(domain.getId() != null ? domain.getId().longValue() : null)
                .userId(domain.getUserId())
                .email(domain.getEmail())
                .activeEmail(domain.getActiveEmail())
                .name(domain.getName())
                .password(domain.getPassword())
                .authority(domain.getAuthority())
                .status(domain.getStatus())
                .deleted(domain.isDeleted())
                .build();
    }
}
