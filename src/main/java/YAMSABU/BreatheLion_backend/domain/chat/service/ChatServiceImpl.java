package YAMSABU.BreatheLion_backend.domain.chat.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatLogDTO.ChatStartResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatLogDTO.ChatStartRequestDTO;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    @Override
    @Transactional
    public ChatStartResponseDTO startChating(ChatStartRequestDTO chatStartRequestDTO){

    }
}
