package YAMSABU.BreatheLion_backend.global.s3;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Config {

    // cloud.aws.s3.bucket 설정이 존재할 때만 AmazonS3 빈 생성
    @Bean
    @ConditionalOnProperty(prefix = "cloud.aws.s3", name = "bucket")
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withRegion(Regions.AP_NORTHEAST_2)
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }
}
