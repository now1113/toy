package site.kimnow.toy.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import site.kimnow.toy.user.domain.UserVerification;
import site.kimnow.toy.user.entity.UserVerificationEntity;
import site.kimnow.toy.user.exception.UserVerificationNotFoundException;
import site.kimnow.toy.user.mapper.UserVerificationMapper;


@Slf4j
@Repository
@RequiredArgsConstructor
public class UserVerificationRepositoryAdapter implements UserVerificationRepository {

    private final UserVerificationJpaRepository userVerificationJpaRepository;
    private final UserVerificationMapper userVerificationMapper;

    @Override
    public void save(UserVerification userVerification) {
        UserVerificationEntity entity = userVerificationMapper.toEntity(userVerification);
        userVerificationJpaRepository.save(entity);
    }

    @Override
    public void delete(UserVerification userVerification) {
        UserVerificationEntity entity = userVerificationMapper.toEntity(userVerification);
        userVerificationJpaRepository.delete(entity);
    }

    @Override
    public UserVerification findByToken(String token) {
        UserVerificationEntity entity = userVerificationJpaRepository.findByToken(token).orElseThrow(
                UserVerificationNotFoundException::new
        );
        return userVerificationMapper.toDomain(entity);
    }
}
