package site.kimnow.toy.common.properties;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Getter
@Setter
@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private long accessTokenExpirationMills;
    private long refreshTokenExpirationMills;
}
