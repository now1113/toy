package site.kimnow.toy.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PermitAllUrl {

    public static String[] getAll() {
        return ALL.clone();
    }

    private static final String[] SWAGGER = {
            "/swagger-ui/**",
            "/v3/api-docs/**"
    };

    private static final String[] AUTH = {
            "/api/v1/user/join",
            "/api/v1/user/login"
    };
    private static final String[] ALL = Stream.of(SWAGGER, AUTH)
            .flatMap(Arrays::stream)
            .toArray(String[]::new);

}
