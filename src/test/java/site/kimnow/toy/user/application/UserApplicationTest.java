package site.kimnow.toy.user.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.dto.request.UserJoinRequest;
import site.kimnow.toy.user.dto.response.UserJoinResponse;
import site.kimnow.toy.user.exception.DuplicateEmailException;
import site.kimnow.toy.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@DisplayName("UserApplication 테스트")
@ExtendWith(SpringExtension.class)
public class UserApplicationTest {

    @InjectMocks
    private UserApplication userApplication;
    @Mock
    private UserService userService;

    @Nested
    @DisplayName("회원가입 성공 케이스")
    class joinSuccess {

        @Test
        @DisplayName("정상 회원가입 시 UserJoinResponse를 반환한다")
        void join_shouldReturnUserJoinResponse() {
            // given
            UserJoinRequest request = UserJoinRequest.of("test@example.com", "홍길동", "!@toto1234", "!@toto1234");

            doNothing().when(userService).join(any(User.class));

            // when
            UserJoinResponse response = userApplication.join(request);

            // then
            assertEquals("홍길동", response.getName());

            verify(userService, times(1)).join(any(User.class));
        }
    }

    @Nested
    @DisplayName("회원가입 실패 케이스")
    class joinFailure {

        @Test
        @DisplayName("중복 이메일일 경우 DuplicateEmailException을 던진다")
        void join_shouldThrowDuplicateEmailException() {
            // given
            UserJoinRequest request = UserJoinRequest.of("test@example.com", "홍길동", "!@toto1234", "!@toto1234");

            doThrow(new DuplicateEmailException(request.getEmail()))
                    .when(userService)
                    .join(any(User.class));

            // when & then
            assertThrows(DuplicateEmailException.class, () -> userApplication.join(request));
        }
    }
}
