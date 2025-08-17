package YAMSABU.BreatheLion_backend.domain.drawer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
        private Long drawerId;
        private String name;
        private String createdAt;
    }
}
