package site.kimnow.toy.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.kimnow.toy.user.entity.UserEntity;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {
    boolean existsByEmail(String email);
    Optional<UserEntity> findByEmail(String email);
}
