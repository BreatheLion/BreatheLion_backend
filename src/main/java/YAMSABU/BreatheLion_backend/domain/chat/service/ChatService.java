package YAMSABU.BreatheLion_backend.domain.chat.service;

import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatAnswerDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatEndRequestDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatEndResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatWithEvidenceDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatMessageListDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatRequestDTO;

public interface ChatService {
    ChatStartResponseDTO startChatting(ChatRequestDTO chatRequestDTO);

    ChatAnswerDTO attachChatting(ChatWithEvidenceDTO chatWithEvidenceDTO);

    ChatMessageListDTO getChattingList(Long recordID);

    ChatEndResponseDTO endChatting(ChatEndRequestDTO chatEndRequestDTO);
}
