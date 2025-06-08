package site.kimnow.toy.jwt.util;

import io.jsonwebtoken.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import static org.mockito.Mockito.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class JwtTokenProviderTest {

    @Mock
    private JwtProperties jwtProperties;
    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        lenient().when(jwtProperties.getSecret()).thenReturn("my-super-safe-secret-key-12345678!!");
        lenient().when(jwtProperties.getAccessTokenExpirationMills()).thenReturn(3600000L);
        lenient().when(jwtProperties.getRefreshTokenExpirationMills()).thenReturn(1209600000L);
    }


    @Nested
    @DisplayName("토큰 생성 시나리오")
    class TokenCreationTests {

        @Test
        @DisplayName("AccessToken은 sub, role, 만료시간이 포함되어야 한다.")
        void createAccessToken() {
            // given
            String userId = "user123";
            String role = "USER";

            // when
            String token = jwtTokenProvider.createAccessToken(userId, role);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            // then
            assertThat(claims.getSubject()).isEqualTo(userId);
            assertThat(claims.get("role", String.class)).isEqualTo(role);
            assertThat(claims.getExpiration().getTime() - claims.getIssuedAt().getTime()).isEqualTo(3600000L);
        }

        @Test
        @DisplayName("RefreshToken은 sub만 포함하고 role은 없어야 한다")
        void createRefreshToken() {
            // given
            String userId = "user123";

            // when
            String token = jwtTokenProvider.createRefreshToken(userId);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // when
            assertThat(claims.getSubject()).isEqualTo(userId);
            assertThat(claims.get("role")).isNull();
        }
    }

    @Nested
    @DisplayName("validateToken() 테스트")
    class ValidateTokenTests {

        @Test
        @DisplayName("정상 토큰일 경우 true를 반환해야 한다")
        void validateToken_withValidToken() {
            // given
            String token = jwtTokenProvider.createAccessToken("test-user", "USER");

            // when
            boolean isValid = jwtTokenProvider.validateToken(token);

            // then
            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("정상 USER 토큰에서 getRole 호출 시 USER가 반환도이어 햔다")
        void getRole_withValidToken() {
            // given
            String token = jwtTokenProvider.createAccessToken("test-user", "USER");

            // when
            String role = jwtTokenProvider.getRole(token);

            // then
            assertThat(role).isEqualTo("USER");
        }

        @Test
        @DisplayName("만료된 토큰일 경우 ExpiredJwtException이 발생해야 한다")
        void validateToken_withExpiredToken() {
            // given
            String expiredToken = createExpiredToken();

            // when-then
            assertThrows(ExpiredJwtException.class, () -> jwtTokenProvider.validateToken(expiredToken));
        }
    }

    @Nested
    @DisplayName("서명 없는 토큰 공격 시나리오")
    class UnsignedTokenTests {

        @Test
        @DisplayName("getUserId 호출 시 JwtException이 발생해야 한다")
        void getUserId_withUnsignedToken() {
            // given
            String unsignedToken = generateUnsignedToken();

            // when-then
            assertThrows(JwtException.class, () -> jwtTokenProvider.getUserId(unsignedToken));
        }

        @Test
        @DisplayName("getRole 호출 시 JwtException이 발생해야 한다.")
        void getRole_withUnsignedToken() {
            // given
            String unsignedToken = generateUnsignedToken();

            // when-then
            assertThrows(JwtException.class, () -> jwtTokenProvider.getRole(unsignedToken));
        }

        @Test
        @DisplayName("validateToken 호출 시 false를 반환해야 한다")
        void validateToken_withUnsignedToken() {
            // given
            String unsignedToken = generateUnsignedToken();

            // when-then
            boolean isValid = jwtTokenProvider.validateToken(unsignedToken);
            assertThat(isValid).isFalse();
        }
    }

    @Nested
    @DisplayName("alg=none 공격 시나리오")
    class NoneAlgTests{

        @Test
        @DisplayName("getUserId 호출 시 예외를 발생시켜야 한다")
        void getUserId_withNoneAlgorithmToken() {
            // given
            String token = createNoneAlgorithmToken();

            // when-then
            assertThrows(JwtException.class, () -> jwtTokenProvider.getUserId(token));
        }

        @Test
        @DisplayName("getRole 호출 시 예외를 발생시켜야 한다")
        void getRole_withNoneAlgorithmToken() {
            // given
            String token = createNoneAlgorithmToken();

            // when-then
            assertThrows(JwtException.class, () -> jwtTokenProvider.getRole(token));
        }

        @Test
        @DisplayName("validateToken 호출 시 false를 반환해야 한다")
        void validateToken_withNoneAlgorithmToken() {
            // given
            String token = createNoneAlgorithmToken();

            // when-then
            boolean isValid = jwtTokenProvider.validateToken(token);
            assertThat(isValid).isFalse();
        }
    }

    @Nested
    @DisplayName("Payload 조작 공격 시나리오 (권한 변조)")
    class RoleTamperingTests {

        @Test
        @DisplayName("getRole 호출 시 JwtException이 발생해야 한다")
        void getRole_withTamperedToken() {
            // given
            String tamperedRoleToken = createTamperedRoleToken();

            // when-then
            assertThrows(JwtException.class, () -> jwtTokenProvider.getRole(tamperedRoleToken));
        }
    }

    @Nested
    @DisplayName("잘못된 형식의 JWT 테스트")
    class MalformedTokenTests {

        @Test
        @DisplayName("형식이 잘못된 토큰일 경우 getUserId 호출 시 JwtException이 발생해야 한다")
        void getUserId_withMalformedToken() {
            // given
            String malformedToken = "not-a-jwt";

            // when-then
            assertThrows(JwtException.class, () -> jwtTokenProvider.getUserId(malformedToken));
        }

        @Test
        @DisplayName("형식이 잘못된 토큰일 경우 getRole 호출 시 JwtException이 발생해야 한다")
        void getRole_withMalformedToken() {
            // given
            String malformedToken = "abc.def";

            // when-then
            assertThrows(JwtException.class, () -> jwtTokenProvider.getRole(malformedToken));
        }

        @Test
        @DisplayName("형식이 잘못된 토큰일 경우 validateToken은 false를 반환해야 한다")
        void  validateToken_withMalformedToken() {
            // given
            String malformedToken = "invalid-token-without-dots";

            // when
            boolean isValid = jwtTokenProvider.validateToken(malformedToken);

            // then
            assertThat(isValid).isFalse();
        }

    }


    private String generateUnsignedToken() {
        String token = jwtTokenProvider.createAccessToken("user123", "USER");
        String[] parts = token.split("\\.");
        return parts[0] + "." + parts[1] + "."; // signature 제거
    }

    private String createNoneAlgorithmToken() {
        String header = Base64.getUrlEncoder().withoutPadding().encodeToString(
                "{\"alg\":\"none\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8)
        );
        String payload = Base64.getUrlEncoder().withoutPadding().encodeToString(
                ("{\"sub\":\"user123\",\"" + "role" + "\":\"ADMIN\"}").getBytes(StandardCharsets.UTF_8));
        return header + "." + payload + ".";
    }

    private String createTamperedRoleToken() {
        String token = jwtTokenProvider.createAccessToken("user123", "USER");
        String[] parts = token.split("\\.");

        // payload 디코딩 후 강제 조작
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
        String tamperedPayloadJson = payloadJson.replace("\"USER\"", "\"ADMIN\"");

        String tamperedPayload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(tamperedPayloadJson.getBytes(StandardCharsets.UTF_8));

        // 조작된 payload + 기존 signature
        return parts[0] + "." + tamperedPayload + "." + parts[2];
    }

    private String createExpiredToken() {
        Date now = new Date();
        Date expiredTime = new Date(now.getTime() - 60_000); // 1분 전

        return Jwts.builder()
                .setSubject("test-user")
                .claim("role", "USER")
                .setIssuedAt(now)
                .setExpiration(expiredTime)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8))
                .compact();
    }

}


