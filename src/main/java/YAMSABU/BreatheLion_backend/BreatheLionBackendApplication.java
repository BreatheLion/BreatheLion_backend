package YAMSABU.BreatheLion_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
//@PropertySource("classpath:secrets.properties")
public class BreatheLionBackendApplication {
    public static void main(String[] args) {
		SpringApplication.run(BreatheLionBackendApplication.class, args);
	}
}
