package YAMSABU.BreatheLion_backend.domain.drawer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
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
}
