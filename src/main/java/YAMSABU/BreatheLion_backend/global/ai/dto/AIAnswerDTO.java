package YAMSABU.BreatheLion_backend.global.ai.dto;

import YAMSABU.BreatheLion_backend.domain.record.entity.RecordCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class AIAnswerDTO {

    // 서랍 요약 기능 + 기관 선택 기능 + 행동 추천 기능
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SOA_DTO {
        @JsonProperty("summary")
        private String summary;

        @JsonProperty("organization_id")
        private List<Long> organizationID; // 최대 2개

        @JsonProperty("care_guide")
        private String care_guide;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LawDTO {
        @JsonProperty(required = true, value = "law_name")
        private String lawName;

        @JsonProperty(required = true, value = "article")
        private String article;

        @JsonProperty(required = true, value = "content")
        private String content;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LawListDTO {
        @JsonProperty(required = true, value = "laws")
        private List<LawDTO> laws;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatSummaryDTO {
        @JsonProperty(value = "title", required = true)
        private String title;

        @JsonProperty(value = "categories", required = true)
        private List<RecordCategory> categories;

        @JsonProperty(value = "content", required = true)
        private String content;

        @JsonProperty(value = "severity", required = true)
        private Integer severity; // 0(낮음), 1(보통), 2(높음)

        @JsonProperty(value = "location", required = true)
        private String location;

        @JsonProperty(value = "occurred_at", required = true)
        private LocalDateTime occurredAt;

        @JsonProperty(value = "assailant", required = true)
        private List<String> assailant;

        @JsonProperty(value = "witness", required = true)
        private List<String> witness;
    }
}
