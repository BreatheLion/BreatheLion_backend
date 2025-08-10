package YAMSABU.BreatheLion_backend.domain.chat.service;

import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatLogDTO.ChatStartResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatLogDTO.ChatStartRequestDTO;

public interface ChatService {
    ChatStartResponseDTO startChating(ChatStartRequestDTO chatStartRequestDTO);
}
