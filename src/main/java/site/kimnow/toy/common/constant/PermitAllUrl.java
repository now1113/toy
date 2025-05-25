package site.kimnow.toy.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PermitAllUrl {

    public static RequestMatcher[] getAll() {
        return Stream.of(SWAGGER, AUTH, ROOT)
                .flatMap(Arrays::stream)
                .toArray(RequestMatcher[]::new);
    }

    private static final RequestMatcher[] SWAGGER = {
            new AntPathRequestMatcher("/swagger-ui/**", "GET"),
            new AntPathRequestMatcher("/v3/api-docs/**", "GET")
    };

    private static final RequestMatcher[] ROOT = {
            new AntPathRequestMatcher("/", "GET")
    };

    private static final RequestMatcher[] AUTH = {
            new AntPathRequestMatcher("/api/v1/auth/login", "POST"),
            new AntPathRequestMatcher("/api/v1/users", "POST")
    };

}
