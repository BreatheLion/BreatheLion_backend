package YAMSABU.BreatheLion_backend.domain.chat.service;

import YAMSABU.BreatheLion_backend.domain.chat.converter.ChatConverter;
import YAMSABU.BreatheLion_backend.domain.chat.entity.Chat;
import YAMSABU.BreatheLion_backend.domain.chat.entity.ChatRole;
import YAMSABU.BreatheLion_backend.domain.chat.entity.Session;
import YAMSABU.BreatheLion_backend.domain.chat.repository.ChatRepository;
import YAMSABU.BreatheLion_backend.domain.chat.repository.SessionRepository;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.record.repository.RecordRepository;

import YAMSABU.BreatheLion_backend.global.ai.service.AIService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartRequestDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final ChatRepository chatRepository;
    private final SessionRepository sessionRepository;
    private final RecordRepository recordRepository;
    private final AIService aiService;

    @Override
    @Transactional
    public ChatStartResponseDTO startChating(ChatStartRequestDTO chatStartRequestDTO){
        // 생성 및 저장
        Record record = new Record();
        Session session = new Session(record);

        recordRepository.save(record);
        sessionRepository.save(session);

        Chat chatRequest = ChatConverter.requestToChat(chatStartRequestDTO,session);
        chatRepository.save(chatRequest);

        // AI 답변 생성
        String aiAnswer = aiService.ChatAnswer(chatRequest.getMessage());
        Chat answerChat = ChatConverter.anwertoChat(aiAnswer, session);

        chatRepository.save(answerChat);

        // DTO 생성 후 반환
        return ChatConverter.toChatStartResponseDTO(session, answerChat);
    }
}
