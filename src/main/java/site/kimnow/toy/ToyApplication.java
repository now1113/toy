package site.kimnow.toy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:secret.properties")
public class ToyApplication {
	public static void main(String[] args) {
		SpringApplication.run(ToyApplication.class, args);
	}

}
