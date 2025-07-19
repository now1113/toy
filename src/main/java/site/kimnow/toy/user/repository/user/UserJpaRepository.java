package site.kimnow.toy.user.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;
import site.kimnow.toy.user.entity.UserEntity;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByActiveEmail(String activeEmail);
    Optional<UserEntity> findByActiveEmail(String email);
}
