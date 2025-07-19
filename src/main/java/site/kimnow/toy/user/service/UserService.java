package site.kimnow.toy.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.domain.UserVerification;
import site.kimnow.toy.user.dto.response.UserResponse;
import site.kimnow.toy.user.exception.DuplicateEmailException;
import site.kimnow.toy.user.query.UserSearchQuery;
import site.kimnow.toy.user.repository.user.UserRepositoryImpl;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepositoryImpl userRepository;

    public void join(User user) {
        validateUser(user);

        user.encodePassword(passwordEncoder);

        userRepository.save(user);
    }

    private void validateUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException(user.getEmail());
        }
    }

    @Transactional
    public void verify(UserVerification userVerification) {
        User user = userRepository.findByEmail(userVerification.getEmail());
        user.completeEmailVerification();

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> queryUsers(UserSearchQuery query) {

        return null;
    }
}
