package site.kimnow.toy.user.repository.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import site.kimnow.toy.common.base.event.DomainEventDispatcher;
import site.kimnow.toy.common.base.jpa.BaseJpaRepository;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.domain.UserId;
import site.kimnow.toy.user.entity.UserEntity;
import site.kimnow.toy.user.exception.UserNotFoundException;
import site.kimnow.toy.user.mapper.UserMapper;

import java.util.Optional;

@Slf4j
@Repository
public class UserRepositoryImpl extends BaseJpaRepository<User, UserId, UserEntity, Long, UserJpaRepository> implements UserRepository {

    public UserRepositoryImpl(UserJpaRepository jpaRepository, UserMapper mapper, DomainEventDispatcher dispatcher) {
        super(jpaRepository, mapper, dispatcher);
    }

    @Override
    protected Long convertId(UserId id) {
        return id.longValue();
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByActiveEmail(email);
    }

    @Override
    public User findByEmail(String email) {
        Optional<UserEntity> entity = jpaRepository.findByActiveEmail(email);

        if (entity.isEmpty()) {
            log.error("해당 이메일을 가진 사용자가 존재하지 않습니다. userEmail: {}", email);
            throw new UserNotFoundException();
        }
        return mapper.toDomain(entity.get());
    }
}
