package site.kimnow.toy.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import site.kimnow.toy.jwt.util.JwtProperties;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class ConfigurationPropertiesConfig {
}
