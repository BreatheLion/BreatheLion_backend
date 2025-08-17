package YAMSABU.BreatheLion_backend.global.s3;

public interface S3Port {
    String generatePresignedGetUrl(String key, int minutes);
    String generatePresignedPutUrl(String key, int minutes);
}
