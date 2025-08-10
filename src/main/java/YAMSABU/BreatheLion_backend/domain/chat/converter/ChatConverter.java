package YAMSABU.BreatheLion_backend.domain.chat.converter;

import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartRequestDTO;
import YAMSABU.BreatheLion_backend.domain.chat.entity.Chat;
import YAMSABU.BreatheLion_backend.domain.chat.entity.ChatRole;
import YAMSABU.BreatheLion_backend.domain.chat.entity.Session;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatConverter {

    /** 사용자가 보낸 첫 메시지 → Chat(user) */
    public static Chat requestToChat(ChatStartRequestDTO dto, Session session) {
        return Chat.builder()
                .session(session)
                .role(ChatRole.user)
                .message(dto.getMessage())
                .build();
    }

    /** AI 답변 → Chat(assistant) */
    public static Chat anwertoChat(String answer, Session session) {
        return Chat.builder()
                .session(session)
                .role(ChatRole.assistant)
                .message(answer)
                .build();
    }

    /** AI 답변 메시지 기준으로 Start 응답 DTO 생성 */
    public static ChatStartResponseDTO toChatStartResponseDTO(Session session, Chat assistantChat) {
        // 현재 날짜/시간
        LocalDateTime now = LocalDateTime.now();
        String messageTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));
        String messageDate = now.format(DateTimeFormatter.ofPattern("MM-dd"));

        return ChatStartResponseDTO.builder()
                .session_id(session.getId())
                .answer(assistantChat.getMessage())
                .message_time(messageTime)
                .message_date(messageDate)
                .build();
    }
}
