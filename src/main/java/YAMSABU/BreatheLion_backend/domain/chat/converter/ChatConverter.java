package YAMSABU.BreatheLion_backend.domain.chat.converter;

import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatMessageListDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatMessageResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatRequestDTO;
import YAMSABU.BreatheLion_backend.domain.chat.entity.Chat;
import YAMSABU.BreatheLion_backend.domain.chat.entity.ChatRole;
import YAMSABU.BreatheLion_backend.domain.chat.entity.Session;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChatConverter {

    // 사용자가 보낸 첫 메시지 → Chat(user)
    public static Chat requestToChat(ChatRequestDTO dto, Session session) {
        return Chat.builder()
                .session(session)
                .role(ChatRole.user)
                .message(dto.getMessage())
                .build();
    }

    // AI 답변 → Chat(assistant)
    public static Chat anwerToChat(String answer, Session session) {
        return Chat.builder()
                .session(session)
                .role(ChatRole.assistant)
                .message(answer)
                .build();
    }

    // AI 답변 메시지 기준으로 Start 응답 DTO 생성
    public static ChatStartResponseDTO toChatStartResponseDTO(Session session, Chat assistantChat) {
        // 현재 날짜/시간
        LocalDateTime now = LocalDateTime.now();
        String messageTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));
        String messageDate = now.format(DateTimeFormatter.ofPattern("yyyy - MM - dd"));

        return ChatStartResponseDTO.builder()
                .sessionId(session.getId())
                .answer(assistantChat.getMessage())
                .messageTime(messageTime)
                .messageDate(messageDate)
                .build();
    }

    public static ChatMessageResponseDTO toChatMessageResponseDTO(String answer){
        LocalDateTime now = LocalDateTime.now();
        String messageTime = now.format(DateTimeFormatter.ofPattern("HH:mm"));
        String messageDate = now.format(DateTimeFormatter.ofPattern("yyyy - MM - dd"));

        return ChatMessageResponseDTO.builder()
                .content(answer)
                .messageTime(messageTime)
                .messageDate(messageDate)
                .build();
    }

    // 전체 조회
    public static ChatMessageListDTO toChatListDTO(Session session, List<Chat> chatList){
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MM-dd");

        List<ChatMessageResponseDTO> messages = chatList.stream()
                .map(c -> ChatMessageResponseDTO.builder()
                        .content(c.getMessage())
                        .messageTime(c.getSendAt().toLocalTime().format(timeFmt))
                        .messageDate(c.getSendAt().toLocalDate().format(dateFmt))
                        .build())
                .toList();

        return ChatMessageListDTO.builder()
                .sessionId(session.getId())
                .messages(messages)
                .build();
    }

    //
}
