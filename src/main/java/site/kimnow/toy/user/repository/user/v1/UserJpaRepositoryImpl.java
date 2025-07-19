package site.kimnow.toy.user.repository.user.v1;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import site.kimnow.toy.user.entity.UserEntity;

import java.util.Optional;

import static site.kimnow.toy.user.entity.QUserEntity.userEntity;

@RequiredArgsConstructor
public class UserJpaRepositoryImpl implements UserJpaRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByEmail(String email) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(userEntity)
                .where(userEntity.activeEmail.eq(email))
                .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public Optional<UserEntity> findByEmail(String email) {
        UserEntity entity = queryFactory
                .selectFrom(userEntity)
                .where(userEntity.email.eq(email),
                        userEntity.deleted.isFalse())
                .fetchFirst();

        return Optional.ofNullable(entity);
    }
}
