package site.kimnow.toy.user.repository.user;

import site.kimnow.toy.user.domain.User;

public interface UserRepository {
    boolean existsByEmail(String email);
    User findByEmail(String userId);
}
