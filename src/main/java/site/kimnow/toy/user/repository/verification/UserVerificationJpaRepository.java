package site.kimnow.toy.user.repository.verification;

import org.springframework.data.jpa.repository.JpaRepository;
import site.kimnow.toy.user.entity.UserVerificationEntity;

import java.util.Optional;

public interface UserVerificationJpaRepository extends JpaRepository<UserVerificationEntity, Long> {
    Optional<UserVerificationEntity> findByToken(String token);
}
