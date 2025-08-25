package YAMSABU.BreatheLion_backend.domain.record.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class RecordDTO {

    // 1. 저장 요청
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RecordSaveRequestDTO {

        private Long recordId;

        private String title;

        private String category;

        @NotBlank
        private String content;

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
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EvidenceSaveRequestDTO {
        @NotBlank
        private String filename;
        @NotBlank
        private String type;
        @NotBlank
        private String s3Key;
    }


    // 3. 최근 기록 확인(조회)
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RecordRecentItemDTO {
        // 상세 기록 진입을 위한 키값
        private Long recordId;

        private Long drawerId;

        private String drawer_title;

        private String record_title;

        private String location;

        private List<String> assailant;

        private LocalDateTime occurredAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RecordRecentResponseDTO {
        private List<RecordRecentItemDTO> records;
    }

    // 4. 상세 조회 응답
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RecordDetailResponseDTO {

        private Long recordId;

        private Long drawerId;

        private String drawer;

        private String category;

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
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RecordDraftRequestDTO {

        private String title;

        private String category;

        private String content;

        private Integer severity;

        private String location;

        private LocalDateTime occurredAt;

        private List<String> assailant;

        private List<String> witness;

        private String drawer;

        private List<EvidenceSaveRequestDTO> evidences;

    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecordTitleUpdateRequestDTO {
        @NotBlank
        private String title;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecordDrawerUpdateRequestDTO {
        private Long drawerId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TimelineResponseDTO {
        @JsonProperty("record_id")
        private Long recordId;
        private String category;
        private String title;
        private String location;
        private Integer severity;
        private String summary;
        @JsonProperty("occurred_at")
        private String occurredAt;
    }
}
