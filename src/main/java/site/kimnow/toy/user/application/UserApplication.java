package site.kimnow.toy.user.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import site.kimnow.toy.common.util.PasswordEncryptor;
import site.kimnow.toy.common.util.RandomIdGenerator;
import site.kimnow.toy.user.command.JoinUser;
import site.kimnow.toy.user.dto.request.UserJoinRequest;
import site.kimnow.toy.user.dto.response.UserJoinResponse;
import site.kimnow.toy.user.service.UserService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserApplication {

    private final UserService userService;

    public UserJoinResponse join(UserJoinRequest dto) {



        String userId = RandomIdGenerator.generate();
        String salt = PasswordEncryptor.generateSalt();

        String encodedPassword = PasswordEncryptor.hash(dto.getPassword(), salt);

        JoinUser command = JoinUser.of(userId, dto.getEmail(), encodedPassword, dto.getName(), salt);
        userService.join(command);

        return UserJoinResponse.from(dto.getName());
    }
}
