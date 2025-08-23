package YAMSABU.BreatheLion_backend.domain;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 API 경로
                .allowedOrigins("http://localhost:5173") // 허용할 Origin
                .allowedMethods("*") // GET, POST, PUT, DELETE, OPTIONS 등 모두
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(false);
    }
}