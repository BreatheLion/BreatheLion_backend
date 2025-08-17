package YAMSABU.BreatheLion_backend.global.s3;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.Duration;
import java.util.Date;

@Service
@ConditionalOnBean(AmazonS3.class) // AmazonS3 빈이 존재할 때만 등록됨
public class S3Service implements S3Port {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public S3Service(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public String generatePresignedGetUrl(String key, int minutes) {
        Date expiration = new Date(System.currentTimeMillis() + Duration.ofMinutes(minutes).toMillis());
        GeneratePresignedUrlRequest req =
                new GeneratePresignedUrlRequest(bucket, key)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(expiration);
        URL url = amazonS3.generatePresignedUrl(req);
        return url.toString();
    }

    @Override
    public String generatePresignedPutUrl(String key, int minutes) {
        Date expiration = new Date(System.currentTimeMillis() + Duration.ofMinutes(minutes).toMillis());
        GeneratePresignedUrlRequest req =
                new GeneratePresignedUrlRequest(bucket, key)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration);
        URL url = amazonS3.generatePresignedUrl(req);
        return url.toString();
    }
}
