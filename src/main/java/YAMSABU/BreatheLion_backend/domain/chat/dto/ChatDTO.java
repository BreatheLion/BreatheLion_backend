package YAMSABU.BreatheLion_backend.domain.chat.dto;

import YAMSABU.BreatheLion_backend.domain.chat.entity.ChatRole;
import YAMSABU.BreatheLion_backend.domain.evidence.dto.EvidenceDTO.EvidenceResponseDTO;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO.EvidenceSaveRequestDTO;
import YAMSABU.BreatheLion_backend.global.ai.dto.AIAnswerDTO.ChatSummaryDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class ChatDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatRequestDTO {
        @NotBlank
        private String message;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatStartResponseDTO {
        @JsonProperty("record_id")
        private Long recordId;
        @JsonProperty("session_id")
        private Long sessionId;
        private String answer;
        @JsonProperty("message_time")
        private String messageTime;
        @JsonProperty("message_date")
        private String messageDate;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatAnswerDTO{
        private String answer;
        @JsonProperty("message_time")
        private String messageTime;
        @JsonProperty("message_date")
        private String messageDate;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatMessageResponseDTO {
        private ChatRole role;
        private String content;
        @JsonProperty("message_time")
        private String messageTime;
        @JsonProperty("message_date")
        private String messageDate;
        private List<EvidenceResponseDTO> evidences;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatMessageListDTO {
        @JsonProperty("session_id")
        private Long sessionId;
        private List<ChatMessageResponseDTO> messages;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChatWithEvidenceDTO {
        @JsonProperty("chat_session_id")
        @NotNull
        private Long chatSessionId;

        private String text;

        @Builder.Default
        @Size(max = 10)
        private List<EvidenceSaveRequestDTO> evidences = new ArrayList<>();
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChatEndRequestDTO {
        @JsonProperty("chat_session_id")
        @NotNull
        private Long chatSessionId;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChatEndResponseDTO {
        @JsonProperty("record_id")
        private Long recordId;

        @JsonProperty("record_detail")
        private ChatSummaryDTO recordDetail;
    }
}