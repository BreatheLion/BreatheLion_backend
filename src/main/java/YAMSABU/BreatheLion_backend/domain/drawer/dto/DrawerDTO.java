package YAMSABU.BreatheLion_backend.domain.drawer.dto;

import YAMSABU.BreatheLion_backend.global.ai.dto.AIAnswerDTO.LawListDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class DrawerDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DrawerDeleteRequestDTO {
        @NotNull(message = "삭제할 id 리스트는 필수입니다.")
        @Size(min = 1, message = "최소 1개 이상의 id가 필요합니다.")
        private List<@NotNull(message = "id에 null이 포함될 수 없습니다.") Long> deleteList;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DrawerCreateRequestDTO {
        @JsonProperty("drawer_name")
        @NotBlank
        private String drawerName;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DrawerResponseDTO {
        @JsonProperty("drawer_id")
        private Long drawerId;
        private String name;
        @JsonProperty("create_at")
        private String createdAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DrawerItemDTO {
        @JsonProperty("drawer_id")
        private Long drawerId;

        private String name;

        @JsonProperty("record_count")
        private Long recordCount;

        @JsonProperty("create_at")
        private String createdAt;

        @JsonProperty("update_at")
        private String updatedAt;

    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DrawerListResponseDTO {
        private List<DrawerItemDTO> drawers;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AIHelpResponseDTO{

        @JsonProperty("drawer_name")
        private String drawerName;

        private List<String> assailant;

        @JsonProperty("record_count")
        private Long recordCount;

        private String summary;

        @JsonProperty("care_guide")
        private String careGuide;

        @JsonProperty("related_laws")
        private LawListDTO relatedLaws;

        private List<OrganizationDTO> organizations;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrganizationDTO {
        private String name;
        private String description;
        private String phone;
        private String url;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DrawerTimelineRequestDTO {
        private String keyword;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DrawerTimelineResponseDTO {
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
