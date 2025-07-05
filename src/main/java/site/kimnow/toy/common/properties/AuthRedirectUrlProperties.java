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
@ConfigurationProperties(prefix = "auth.redirect-url")
public class AuthRedirectUrlProperties {
    private String success;
    private String fail;
}
