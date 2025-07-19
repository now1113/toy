package site.kimnow.toy.user.repository.verification;

import org.springframework.stereotype.Repository;
import site.kimnow.toy.common.base.event.DomainEventDispatcher;
import site.kimnow.toy.common.base.jpa.BaseJpaRepository;
import site.kimnow.toy.user.domain.UserVerification;
import site.kimnow.toy.user.domain.UserVerificationId;
import site.kimnow.toy.user.entity.UserVerificationEntity;
import site.kimnow.toy.user.exception.UserVerificationNotFoundException;
import site.kimnow.toy.user.mapper.UserVerificationMapper;

@Repository
public class UserVerificationRepositoryImpl extends BaseJpaRepository<
        UserVerification,
        UserVerificationId,
        UserVerificationEntity,
        Long,
        UserVerificationJpaRepository> implements UserVerificationRepository {

    public UserVerificationRepositoryImpl(UserVerificationJpaRepository jpaRepository, UserVerificationMapper mapper, DomainEventDispatcher dispatcher) {
        super(jpaRepository, mapper, dispatcher);
    }

    @Override
    protected Long convertId(UserVerificationId id) {
        return id.longValue();
    }

    @Override
    public UserVerification findByToken(String token) {
        UserVerificationEntity entity = jpaRepository.findByToken(token).orElseThrow(
                UserVerificationNotFoundException::new
        );
        return mapper.toDomain(entity);
    }
}
