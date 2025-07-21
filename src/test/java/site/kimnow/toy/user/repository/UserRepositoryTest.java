package site.kimnow.toy.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import site.kimnow.toy.common.base.event.SpringDomainEventDispatcher;
import site.kimnow.toy.user.domain.User;
import site.kimnow.toy.user.enums.UserAuthority;
import site.kimnow.toy.user.exception.UserNotFoundException;
import site.kimnow.toy.user.mapper.UserMapper;
import site.kimnow.toy.user.repository.user.UserJpaRepository;
import site.kimnow.toy.user.repository.user.UserRepositoryImpl;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@Import({UserRepositoryImpl.class, UserMapper.class, SpringDomainEventDispatcher.class})
@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {

    @Autowired
    private UserRepositoryImpl userRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    private User testUser;

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpw");

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

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
        userRepository.save(testUser);

        // then
        assertThat(userRepository.existsByEmail("test@example.com")).isTrue();
    }

    @Test
    @DisplayName("existsByEmail() - 저장된 이메일이면 true 반환")
    void existsByEmail_whenUserExists_thenTrue() {
        // given
        userRepository.save(testUser);

        // when
        boolean exists = userRepository.existsByEmail("test@example.com");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByEmail() - 존재하지 않으면 false 반환")
    void existsByEmail_whenUserNotExists_thenFalse() {
        // when
        boolean exists = userRepository.existsByEmail("notfound@example.com");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("findByEmail() - 존재하는 이메일이면 User 반환")
    void findByEmail_whenExists_thenReturnUser() {
        // given
        userRepository.save(testUser);

        // when
        User user = userRepository.findByEmail(testUser.getEmail());

        // then
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(user.getName()).isEqualTo(testUser.getName());
    }

    @Test
    @DisplayName("findByEmail() - 존재하지 않으면 예외 발생")
    void findByEmail_whenNotExists_thenThrow() {
        // expect
        assertThatThrownBy(() -> userRepository.findByEmail("none@example.com"))
                .isInstanceOf(UserNotFoundException.class);
    }

}
