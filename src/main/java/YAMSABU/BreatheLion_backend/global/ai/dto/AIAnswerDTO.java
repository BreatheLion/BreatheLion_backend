package YAMSABU.BreatheLion_backend.global.ai.dto;

import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.OrganizationDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
