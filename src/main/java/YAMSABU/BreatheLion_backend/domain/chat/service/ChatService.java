package YAMSABU.BreatheLion_backend.domain.chat.service;

import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartRequestDTO;

public interface ChatService {
    ChatStartResponseDTO startChatting(ChatStartRequestDTO chatStartRequestDTO);
}
