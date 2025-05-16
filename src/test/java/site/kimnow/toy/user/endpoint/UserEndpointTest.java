package site.kimnow.toy.user.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import site.kimnow.toy.user.application.UserApplication;
import site.kimnow.toy.user.dto.request.UserJoinRequest;
import site.kimnow.toy.user.dto.response.UserJoinResponse;
import site.kimnow.toy.user.exception.DuplicateEmailException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@DisplayName("UserEndpoint 테스트")
@WebMvcTest(UserEndpoint.class)
@AutoConfigureMockMvc
public class UserEndpointTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserApplication userApplication;
    private final ObjectMapper om = new ObjectMapper();

    @Nested
    @DisplayName("회원가입 성공 케이스")
    class JoinSuccess {

        @Test
        @DisplayName("정상적인 요청 시 200 OK와 환영 메세지를 반환한다.")
        void joinSuccess() throws Exception {
            // given
            UserJoinRequest request = UserJoinRequest.of("test@example.com", "홍길동", "!@toto1234", "!@toto1234");
            UserJoinResponse response = UserJoinResponse.from(request.getName());

            given(userApplication.join(any(UserJoinRequest.class))).willReturn(response);

            // when & then
            mockMvc.perform(MockMvcRequestBuilders.post("/api/user/v1/join")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.name").value("홍길동"))
                    .andExpect(jsonPath("$.message").value("홍길동님 회원가입을 환영합니다."));
        }

    }

    @Nested
    @DisplayName("회원가입 실패 케이스")
    class JoinFailure {

        @Test
        @DisplayName("이미 존재하는 이메일로 회원가입 시 409 Conflict와 에러 메세지를 반환한다")
        void joinDuplicateEmail() throws Exception {
            // given
            UserJoinRequest request = UserJoinRequest.of("test@example.com", "홍길동", "!@toto1234", "!@toto1234");

            given(userApplication.join(any(UserJoinRequest.class)))
                    .willThrow(new DuplicateEmailException(request.getEmail()));

            // when & then
            mockMvc.perform(MockMvcRequestBuilders.post("/api/user/v1/join")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(om.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("이미 사용 중인 이메일입니다."));
        }
    }
}
