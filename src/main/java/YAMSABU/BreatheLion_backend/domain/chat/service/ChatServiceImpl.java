package YAMSABU.BreatheLion_backend.domain.chat.service;

import YAMSABU.BreatheLion_backend.domain.chat.converter.ChatConverter;
import YAMSABU.BreatheLion_backend.domain.chat.entity.Chat;
import YAMSABU.BreatheLion_backend.domain.chat.entity.Session;
import YAMSABU.BreatheLion_backend.domain.chat.repository.ChatRepository;
import YAMSABU.BreatheLion_backend.domain.chat.repository.SessionRepository;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.record.repository.RecordRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.api.common.OpenAiApiClientErrorException;
import org.springframework.stereotype.Service;

import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartRequestDTO;


@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final ChatRepository chatRepository;
    private final SessionRepository sessionRepository;
    private final RecordRepository recordRepository;

    private final ChatClient chatClient;

    public String generate(String text){

    }



    @Override
    @Transactional
    public ChatStartResponseDTO startChating(ChatStartRequestDTO chatStartRequestDTO){
        Record record = new Record();
        Session session = new Session(record);

        recordRepository.save(record);
        sessionRepository.save(session);

        Chat chat = ChatConverter.RequesttoChat(chatStartRequestDTO,session);
        chatRepository.save(chat);
        //  여기에 대해서 응답 받고


        return null;
    }
}
