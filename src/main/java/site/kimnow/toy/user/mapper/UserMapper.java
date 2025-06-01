package site.kimnow.toy.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.entity.UserEntity;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "idx", ignore = true)
    UserEntity toEntity(User user);
    User toDomain(UserEntity userEntity);
}
