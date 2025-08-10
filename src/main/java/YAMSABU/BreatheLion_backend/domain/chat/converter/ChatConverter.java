package YAMSABU.BreatheLion_backend.domain.chat.converter;

import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartRequestDTO;
import YAMSABU.BreatheLion_backend.domain.chat.entity.Chat;
import YAMSABU.BreatheLion_backend.domain.chat.entity.ChatRole;
import YAMSABU.BreatheLion_backend.domain.chat.entity.Session;

public class ChatConverter {
    public static Chat RequesttoChat (ChatStartRequestDTO chatStartRequestDTO, Session session){
        Chat chat = Chat.builder()
                .session(session)
                .role(ChatRole.user)
                .message(chatStartRequestDTO.getMessage())
                .build();
        return chat;
    }
}
