package site.kimnow.toy.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.exception.DuplicateEmailException;
import site.kimnow.toy.user.repository.UserRepositoryAdapter;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepositoryAdapter userRepositoryAdapter;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void join(User user) {
        validateUser(user);
        User encodedUser = user.encodePassword(passwordEncoder);
        userRepositoryAdapter.save(encodedUser);
    }

    private void validateUser(User user) {
        if (userRepositoryAdapter.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException(user.getEmail());
        }
    }
}
