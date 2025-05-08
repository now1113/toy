package site.kimnow.toy.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.entity.UserEntity;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository{

    private final UserJpaRepository userJpaRepository;

    @Override
    public void save(User user) {
        UserEntity entity = UserEntity.from(user);
        userJpaRepository.save(entity);
    }
}
