package site.kimnow.toy.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.entity.UserEntity;
import site.kimnow.toy.user.exception.UserNotFoundException;
import site.kimnow.toy.user.mapper.UserMapper;

import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository{

    private final UserJpaRepository userJpaRepository;
    private final UserMapper userMapper;

    @Override
    public void save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        userJpaRepository.save(entity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    @Override
    public User findByUserId(String email) {
        Optional<UserEntity> entity = userJpaRepository.findByEmail(email);

        if (entity.isEmpty()) {
            log.error("해당 이메일을 가진 사용자가 존재하지 않습니다. userEmail: {}", email);
            throw new UserNotFoundException();
        }

        UserEntity userEntity = entity.get();

        return userMapper.toDomain(userEntity);
    }
}
