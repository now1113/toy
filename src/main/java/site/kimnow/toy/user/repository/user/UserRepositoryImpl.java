package site.kimnow.toy.user.repository.user;

import org.springframework.stereotype.Repository;
import site.kimnow.toy.common.base.jpa.BaseRepository;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.domain.UserId;
import site.kimnow.toy.user.entity.UserEntity;
import site.kimnow.toy.user.mapper.UserMapper;

@Repository
public class UserRepositoryImpl extends BaseRepository<User, UserId, UserEntity, Long, UserJpaRepository> implements UserRepository {

    public UserRepositoryImpl(UserJpaRepository jpaRepository, UserMapper mapper) {
        super(jpaRepository, mapper);
    }

    @Override
    protected Long convertId(UserId id) {
        return id.longValue();
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public User findByEmail(String userId) {
        return null;
    }
}
