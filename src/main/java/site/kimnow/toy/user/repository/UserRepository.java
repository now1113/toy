package site.kimnow.toy.user.repository;

import site.kimnow.toy.user.domain.User;

public interface UserRepository {
    void save(User user);
    boolean existsByEmail(String email);
    User findByUserId(String userId);
}
