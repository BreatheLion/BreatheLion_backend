package YAMSABU.BreatheLion_backend.global.s3;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service               // <-- 빈 등록
@Primary               // <-- 여러 구현체가 있어도 기본 주입 대상
public class MockS3Service implements S3Port {

    @Override
    public String generatePresignedGetUrl(String key, int minutes) {
        // 로컬/테스트용 가짜 URL
        return "http://localhost/mock-s3/get/" + key + "?expires=" + minutes;
    }

    @Override
    public String generatePresignedPutUrl(String key, int minutes) {
        return "http://localhost/mock-s3/put/" + key + "?expires=" + minutes;
    }
}
