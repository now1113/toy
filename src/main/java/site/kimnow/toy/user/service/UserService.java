package site.kimnow.toy.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.kimnow.toy.user.command.JoinUser;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.repository.UserRepositoryAdapter;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepositoryAdapter userRepositoryAdapter;

    public void join(JoinUser command) {
        User user = User.from(command);
        userRepositoryAdapter.save(user);
    }

}
