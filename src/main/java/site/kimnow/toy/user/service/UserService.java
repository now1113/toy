package site.kimnow.toy.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.repository.UserRepositoryAdapter;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepositoryAdapter userRepositoryAdapter;

    @Transactional
    public void join(User user) {
        userRepositoryAdapter.save(user);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepositoryAdapter.existsByEmail(email);
    }

}
