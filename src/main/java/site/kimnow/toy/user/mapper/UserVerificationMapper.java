package site.kimnow.toy.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import site.kimnow.toy.user.domain.UserVerification;
import site.kimnow.toy.user.entity.UserVerificationEntity;

@Mapper(componentModel = "spring")
public interface UserVerificationMapper {

    @Mapping(target = "idx", ignore = true)
    UserVerificationEntity toEntity(UserVerification userVerification);
    UserVerification toDomain(UserVerificationEntity userVerificationEntity);
}
