package YAMSABU.BreatheLion_backend.domain.chat.service;

import YAMSABU.BreatheLion_backend.domain.chat.entity.Session;
import YAMSABU.BreatheLion_backend.domain.chat.repository.ChatRepository;
import YAMSABU.BreatheLion_backend.domain.chat.repository.SessionRepository;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.record.repository.RecordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatLogDTO.ChatStartResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatLogDTO.ChatStartRequestDTO;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final ChatRepository chatRepository;
    private final SessionRepository sessionRepository;
    private final RecordRepository recordRepository;
    @Override
    @Transactional
    public ChatStartResponseDTO startChating(ChatStartRequestDTO chatStartRequestDTO){
        // 메세지를 받음
        Record record = new Record();
        Session session = new Session();

        recordRepository.save(record);
        sessionRepository.save(session);



    }
}
