package YAMSABU.BreatheLion_backend.domain.record.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO.*;

import java.time.LocalDateTime;
import java.util.List;

public class RecordDTO {

    // 1. 저장 요청
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    // 자바 카멜케이스 코드를 JSON일때 스네이크케이스로 자동변환해주는 역할
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RecordSaveRequestDTO {

        private Long recordId;

        private String title;

        private List<String> categories;

        @NotBlank
        private String content;

        @Min(1) @Max(5)
        private Integer severity;

        @NotBlank
        private String location;

        private LocalDateTime createdAt;

        @NotNull
        private LocalDateTime occurredAt;

        private List<String> assailant;

        private List<String> witness;

        private String drawer;

        private List<EvidenceSaveRequestDTO> evidences;
        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class EvidenceSaveRequestDTO {
            @NotBlank
            private String filename;
            @NotBlank
            private String type;
            // Presigned URL이 아니라 S3Key만 저장
            @NotBlank
            private String s3Key;
        }
    }

//    controller에서 받는 방식으로 변경
//    // 2. 저장 응답
//    @Getter
//    @Builder
//    @AllArgsConstructor
//    @NoArgsConstructor
//    // 자바 카멜케이스 코드를 JSON일때 스네이크케이스로 자동변환해주는 역할
//    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
//    public static class RecordSaveResponseDTO {
//
//        // 서랍에 기록이 만들어졌어요.
//        private String message;
//    }

    // 3. 최근 기록 확인(조회)
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    // 자바 카멜케이스 코드를 JSON일때 스네이크케이스로 자동변환해주는 역할
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RecordRecentItemDTO {

        // 상세 기록 진입을 위한 키값
        private Long recordId;

        private Long drawerId;

        private String drawer;

        private String location;

        private List<String> assailant;

        private LocalDateTime createdAt;

        private String summary;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    // 자바 카멜케이스 코드를 JSON일때 스네이크케이스로 자동변환해주는 역할
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RecordRecentResponseDTO {
        private List<RecordRecentItemDTO> records;
    }

    // 4. 상세 조회 응답
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    // 자바 카멜케이스 코드를 JSON일때 스네이크케이스로 자동변환해주는 역할
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RecordDetailResponseDTO {

        private Long recordId;

        private Long drawerId;

        // 제목 대신 서랍이름으로 상세기록에 뜨는거 맞나?
        private String drawer;

        private List<String> categories;

        private String title;

        private String content;

        private Integer severity;

        private String location;

        private LocalDateTime occurredAt;

        private LocalDateTime createdAt;

        private LocalDateTime updatedAt;

        private List<String> assailant;

        private List<String> witness;

        private List<EvidenceItemDTO> evidences;

        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        // 자바 카멜케이스 코드를 JSON일때 스네이크케이스로 자동변환해주는 역할
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public static class EvidenceItemDTO {
            private Long id;
            private Long recordId;
            private String type;
            private String filename;
            private String s3Url;
            private LocalDateTime uploadedAt;
        }
    }

    // 5. 초안 수정 요청(DRAFT)
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    // 자바 카멜케이스 코드를 JSON일때 스네이크케이스로 자동변환해주는 역할
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RecordDraftRequestDTO {

        private String title;

        private List<String> categories;

        private String content;

        private Integer severity;

        private String location;

        private LocalDateTime occurredAt;

        private List<String> assailant;

        private List<String> witness;

        private String drawer;

        private List<RecordSaveRequestDTO.EvidenceSaveRequestDTO> evidences;
    }

}
