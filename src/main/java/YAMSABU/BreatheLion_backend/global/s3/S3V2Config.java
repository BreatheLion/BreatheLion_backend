package YAMSABU.BreatheLion_backend.global.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3V2Config {

    @Bean
    Region awsRegion(@Value("${spring.cloud.aws.region.static}") String region) {
        return Region.of(region);
    }

    // 삭제같은 S3 API호출에 사용
    @Bean
    S3Client s3Client(Region region) {
        return S3Client.builder()
                .region(region)
                .build();
    }

    // GET, PUT 용
    @Bean
    S3Presigner s3Presigner(Region region) {
        return S3Presigner.builder()
                .region(region)
                .build();
    }
}

