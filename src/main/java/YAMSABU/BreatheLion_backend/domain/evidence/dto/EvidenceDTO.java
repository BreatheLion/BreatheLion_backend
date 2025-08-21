package YAMSABU.BreatheLion_backend.domain.evidence.dto;

import YAMSABU.BreatheLion_backend.domain.evidence.entity.EvidenceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class EvidenceDTO {

    // presigned url 요청 DTO (fileName 제거)
    @Getter
    public static class EvidencePresignedUrlRequestDTO {
        private String prefix;        // S3 폴더 경로
        private String contentType;   // MIME 타입 (image/png, audio/mpeg 등)
        private Long contentLength;   // 파일 크기
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EvidenceResponseDTO {
        private String url;
        private EvidenceType type;
        private String filename;
    }
}
