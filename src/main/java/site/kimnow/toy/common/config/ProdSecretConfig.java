package site.kimnow.toy.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("prod")
@PropertySource(value = "file:/config/secret.properties", ignoreResourceNotFound = true)
public class ProdSecretConfig {
}
