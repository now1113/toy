package site.kimnow.toy.user.mapper;

import org.springframework.stereotype.Component;
import site.kimnow.toy.common.base.mapper.DomainEntityMapper;
import site.kimnow.toy.user.domain.UserId;
import site.kimnow.toy.user.domain.UserVerification;
import site.kimnow.toy.user.domain.UserVerificationId;
import site.kimnow.toy.user.entity.UserVerificationEntity;

@Component
public class UserVerificationMapper implements DomainEntityMapper<UserVerification, UserVerificationEntity> {

    @Override
    public UserVerification toDomain(UserVerificationEntity entity) {
        return UserVerification.builder()
                .id(entity.getId() != null ? new UserVerificationId(entity.getId()) : null)
                .email(entity.getEmail())
                .token(entity.getToken())
                .expireDate(entity.getExpireDate())
                .build();
    }

    @Override
    public UserVerificationEntity toEntity(UserVerification domain) {
        return UserVerificationEntity.builder()
                .id(domain.getId() != null ? domain.getId().longValue() : null)
                .email(domain.getEmail())
                .token(domain.getToken())
                .expireDate(domain.getExpireDate())
                .build();
    }
}
