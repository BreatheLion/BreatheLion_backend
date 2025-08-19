package YAMSABU.BreatheLion_backend.global.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class S3V2Config {

    @Bean
    Region awsRegion(@Value("${spring.cloud.aws.region.static}") String region) {
        return Region.of(region);
    }

    @Bean
    AwsCredentialsProvider awsCredentialsProvider(
            @Value("${spring.cloud.aws.credentials.access-key}") String accessKey,
            @Value("${spring.cloud.aws.credentials.secret-key}") String secretKey
            // 세션토큰 쓰면 @Value("${spring.cloud.aws.credentials.session-token:}") String session
    ) {
        // 세션 토큰 없다는 가정
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
        // 세션토큰 사용 시: AwsSessionCredentials.create(accessKey, secretKey, session)
    }

    // 삭제같은 S3 API호출에 사용
    @Bean
    S3Client s3Client(Region region, AwsCredentialsProvider creds) {
        return S3Client.builder()
                .region(region)
                .credentialsProvider(creds)
                .build();
    }

    // GET, PUT 용
    @Bean
    S3Presigner s3Presigner(Region region, AwsCredentialsProvider creds) {
        return S3Presigner.builder()
                .region(region)
                .credentialsProvider(creds)
                .build();
    }
}

