package site.kimnow.toy.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.exception.DuplicateEmailException;
import site.kimnow.toy.user.repository.user.v1.UserRepositoryAdapter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@DisplayName("UserService 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepositoryAdapter userRepositoryAdapter;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("join()")
    class Describe_join {

        @Test
        @DisplayName("중복된 이메일이 없으면 비밀번호를 인코딩하고 저장한다")
        void join_successfully() {
            // given
            User rawUser = mock(User.class);
            User encodedUser = mock(User.class);

            given(rawUser.getEmail()).willReturn("test@example.com");
            given(rawUser.getPassword()).willReturn("plain-password");
            given(passwordEncoder.encode("plain-password")).willReturn("encoded-password");
            given(rawUser.withEncodedPassword("encoded-password")).willReturn(encodedUser);
            given(userRepositoryAdapter.existsByEmail("test@example.com")).willReturn(false);

            // when
            userService.join(rawUser);

            // then
            verify(userRepositoryAdapter).save(encodedUser);
        }

        @Test
        @DisplayName("중복된 이메일이면 DuplicateEmailException이 발생한다")
        void join_withDuplicateEmail() {
            // given
            User user = mock(User.class);
            given(user.getEmail()).willReturn("duplicate@example.com");
            given(userRepositoryAdapter.existsByEmail("duplicate@example.com")).willReturn(true);

            // when-then
            assertThrows(DuplicateEmailException.class, () -> userService.join(user));
        }
    }

}
