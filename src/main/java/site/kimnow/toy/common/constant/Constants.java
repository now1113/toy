package site.kimnow.toy.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String REDIS_REFRESH_PREFIX = "refresh:";
    public static final String REDIS_USER_ROLE_PREFIX = "userRole:";
    public static final String DEFAULT_SAME_SITE = "Lax";
    public static final String LOGIN_URL = "/api/v1/auth/login";

    public static final String EMAIL_TITLE = "[TOY] 이메일 인증 요청";
}
