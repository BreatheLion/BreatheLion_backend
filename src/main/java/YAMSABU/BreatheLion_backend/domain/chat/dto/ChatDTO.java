package YAMSABU.BreatheLion_backend.domain.chat.dto;

import YAMSABU.BreatheLion_backend.domain.chat.entity.ChatRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ChatDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatStartRequestDTO {
        @NotBlank
        private String message;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatStartResponseDTO {
        private Long session_id;
        private String answer;
        private String message_time;
        private String message_date;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatMessageResponseDTO {
        private String content;
        private ChatRole role;
        private String message_time;
        private String message_date;
    }


    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatMessageListDTO {
        private Long session_id;
        private List<ChatMessageResponseDTO> messages;
    }
}