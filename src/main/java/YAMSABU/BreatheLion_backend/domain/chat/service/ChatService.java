package YAMSABU.BreatheLion_backend.domain.chat.service;

import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatMessageResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatWithEvidenceDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatMessageListDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatRequestDTO;

public interface ChatService {
    ChatStartResponseDTO startChatting(ChatRequestDTO chatRequestDTO);

    ChatMessageResponseDTO attachChatting(ChatWithEvidenceDTO chatWithEvidenceDTO);

    ChatMessageListDTO getChatList(Long recordID);
}
