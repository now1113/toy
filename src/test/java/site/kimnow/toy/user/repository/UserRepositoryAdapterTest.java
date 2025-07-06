package site.kimnow.toy.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.enums.UserAuthority;
import site.kimnow.toy.user.exception.UserNotFoundException;
import site.kimnow.toy.user.mapper.UserMapperImpl;
import site.kimnow.toy.user.repository.user.UserJpaRepository;
import site.kimnow.toy.user.repository.user.UserRepositoryAdapter;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@Import({UserRepositoryAdapter.class, UserMapperImpl.class})
@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryAdapterTest {

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;
    @Autowired
    private UserJpaRepository userJpaRepository;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId("randomId")
                .email("test@example.com")
                .name("테스트")
                .password("en-password")
                .authority(UserAuthority.ROLE_USER.getInfo())
                .deleted(false)
                .createTime(LocalDateTime.now())
                .modifyTime(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("save() 호출 시 UserEntity로 변환되어 저장된다")
    void save_shouldConvertToEntityAndPersist() {
        // when
        userRepositoryAdapter.save(testUser);

        // then
        assertThat(userJpaRepository.existsByEmail("test@example.com")).isTrue();
    }

    @Test
    @DisplayName("existsByEmail() - 저장된 이메일이면 true 반환")
    void existsByEmail_whenUserExists_thenTrue() {
        // given
        userRepositoryAdapter.save(testUser);

        // when
        boolean exists = userRepositoryAdapter.existsByEmail("test@example.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByEmail() - 존재하지 않으면 false 반환")
    void existsByEmail_whenUserNotExists_thenFalse() {
        // when
        boolean exists = userRepositoryAdapter.existsByEmail("notfound@example.com");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("findByEmail() - 존재하는 이메일이면 User 반환")
    void findByEmail_whenExists_thenReturnUser() {
        // given
        userRepositoryAdapter.save(testUser);

        // when
        User user = userRepositoryAdapter.findByEmail(testUser.getEmail());

        // then
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(user.getName()).isEqualTo(testUser.getName());
    }

    @Test
    @DisplayName("findByEmail() - 존재하지 않으면 예외 발생")
    void findByEmail_whenNotExists_thenThrow() {
        // expect
        assertThatThrownBy(() -> userRepositoryAdapter.findByEmail("none@example.com"))
                .isInstanceOf(UserNotFoundException.class);
    }

}
