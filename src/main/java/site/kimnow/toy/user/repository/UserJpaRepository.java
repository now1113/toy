package site.kimnow.toy.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.kimnow.toy.user.entity.UserEntity;

public interface UserJpaRepository extends JpaRepository<UserEntity, String> {
}
