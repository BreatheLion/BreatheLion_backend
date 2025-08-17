package YAMSABU.BreatheLion_backend.domain.chat.service;

import YAMSABU.BreatheLion_backend.domain.chat.converter.ChatConverter;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO;
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

import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatMessageListDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartRequestDTO;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final ChatRepository chatRepository;
    private final SessionRepository sessionRepository;
    private final RecordRepository recordRepository;
    private final AIService aiService;

    @Override
    @Transactional
    public ChatStartResponseDTO startChatting(ChatStartRequestDTO chatStartRequestDTO){
        // 생성 및 저장
        Record record = new Record();
        Session session = new Session(record);

        recordRepository.save(record);
        sessionRepository.save(session);

        Chat chatRequest = ChatConverter.requestToChat(chatStartRequestDTO,session);
        chatRepository.save(chatRequest);

        // AI 답변 생성
        String aiAnswer = aiService.ChatAnswer(chatRequest.getMessage());
        Chat answerChat = ChatConverter.anwerToChat(aiAnswer, session);

        chatRepository.save(answerChat);

        // DTO 생성 후 반환
        return ChatConverter.toChatStartResponseDTO(session, answerChat);
    }

    @Override
    @Transactional
    public ChatMessageListDTO getChatList(Long recordID){

        Record record = recordRepository.findById(recordID)
                .orElseThrow(() -> new IllegalArgumentException("Record not found: " + recordID));
        Session session = record.getSession();
        List<Chat> chatList = session.getChats();

        return ChatConverter.toChatListDTO(session,chatList);
    }
}
