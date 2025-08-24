package YAMSABU.BreatheLion_backend;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class BreatheLionBackendApplication {

    @PostConstruct
    public void init() {
        // 한국 표준시(Asia/Seoul)로 타임존 설정
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }

    public static void main(String[] args) {
		SpringApplication.run(BreatheLionBackendApplication.class, args);
	}
}
