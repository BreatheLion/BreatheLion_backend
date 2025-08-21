package YAMSABU.BreatheLion_backend.global.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3FileService {

    private final S3Client s3Client;
    private final S3Presigner s3presigner;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;                    // 커스텀 키이므로 @Value로 주입


    // presigned url + s3Key 반환용 DTO
    public static class PresignedUrlResponse {
        public final String presignedUrl;
        public final String s3Key;
        public PresignedUrlResponse(String presignedUrl, String s3Key) {
            this.presignedUrl = presignedUrl;
            this.s3Key = s3Key;
        }
    }

    // contentType을 파라미터로 받아 동적으로 처리
    public PresignedUrlResponse getPutPreSignedUrl(String prefix, String contentType, Long contentLength, String ext) {
        String fileName = java.util.UUID.randomUUID() + "." + ext;
        String filePath = prefix + "/" + fileName;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(filePath)
                .contentType(contentType)
                .contentLength(contentLength)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(2)) // TTL 2분
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3presigner.presignPutObject(presignRequest);
        String url = presignedRequest.url().toString();
        log.info("[S3FileService] getPutPreSignedUrl: {}", url);
        return new PresignedUrlResponse(url, filePath);
    }


    // S3 key 기반 삭제
    public void deleteByKey(String key) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build());
            log.info("[S3FileService] deleteByKey: s3://{}/{}", bucket, key);
        } catch (Exception e) {
            log.error("[S3FileService] deleteByKey error: {}", e.getMessage());
            throw new RuntimeException("S3 파일 삭제 실패: " + key, e);
        }
    }

    public String getGetPreSignedUrlByKey(String s3Key, int minutes) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(minutes))
                .getObjectRequest(b -> b.bucket(bucket).key(s3Key))
                .build();
        PresignedGetObjectRequest presignedRequest = s3presigner.presignGetObject(presignRequest);
        String url = presignedRequest.url().toString();
        log.info("[S3FileService] getGetPreSignedUrlByKey: {}", url);
        return url;
    }
}
