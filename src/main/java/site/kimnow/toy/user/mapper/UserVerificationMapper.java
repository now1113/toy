package site.kimnow.toy.user.mapper;

import org.mapstruct.Mapper;
import site.kimnow.toy.user.domain.UserVerification;
import site.kimnow.toy.user.entity.UserVerificationEntity;

@Mapper(componentModel = "spring")
public interface UserVerificationMapper {

    UserVerificationEntity toEntity(UserVerification userVerification);
    UserVerification toDomain(UserVerificationEntity userVerificationEntity);
}
