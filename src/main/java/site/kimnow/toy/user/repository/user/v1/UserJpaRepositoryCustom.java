package site.kimnow.toy.user.repository.user.v1;

import site.kimnow.toy.user.entity.UserEntity;

import java.util.Optional;

public interface UserJpaRepositoryCustom {

    boolean existsByEmail(String email);
    Optional<UserEntity> findByEmail(String email);
}
