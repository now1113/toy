package site.kimnow.toy.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("local")
@PropertySource("classpath:secret.properties")
public class LocalSecretConfig {
}
