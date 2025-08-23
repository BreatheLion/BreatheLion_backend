package YAMSABU.BreatheLion_backend.domain.chat.service;

import YAMSABU.BreatheLion_backend.domain.chat.converter.ChatConverter;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatAnswerDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatEndRequestDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatEndResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.entity.Chat;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatMessageResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatWithEvidenceDTO;
import YAMSABU.BreatheLion_backend.domain.chat.entity.Session;
import YAMSABU.BreatheLion_backend.domain.chat.repository.ChatRepository;
import YAMSABU.BreatheLion_backend.domain.chat.repository.SessionRepository;
import YAMSABU.BreatheLion_backend.domain.evidence.dto.EvidenceDTO.EvidenceResponseDTO;
import YAMSABU.BreatheLion_backend.domain.evidence.entity.Evidence;
import YAMSABU.BreatheLion_backend.domain.evidence.repository.EvidenceRepository;
import YAMSABU.BreatheLion_backend.domain.record.converter.RecordConverter;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO.EvidenceSaveRequestDTO;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.record.repository.RecordRepository;

import YAMSABU.BreatheLion_backend.global.ai.dto.AIAnswerDTO.ChatSummaryDTO;
import YAMSABU.BreatheLion_backend.global.ai.service.AIService;
import YAMSABU.BreatheLion_backend.global.s3.S3FileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatMessageListDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatRequestDTO;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService{

    private final ChatRepository chatRepository;
    private final SessionRepository sessionRepository;
    private final RecordRepository recordRepository;
    private final EvidenceRepository evidenceRepository;
    private final AIService aiService;
    private final S3FileService s3FileService;


    @Override
    @Transactional
    public ChatStartResponseDTO startChatting(ChatRequestDTO chatRequestDTO){
        // 생성 및 저장
        Record record = new Record();
        Session session = new Session(record);

        recordRepository.save(record);
        sessionRepository.save(session);

        Chat chatRequest = ChatConverter.requestToChat(chatRequestDTO,session);
        chatRepository.save(chatRequest);

        // AI 답변 생성
        String aiAnswer = aiService.ChatAnswer(chatRequest.getMessage());
        Chat answerChat = ChatConverter.anwerToChat(aiAnswer, session);

        chatRepository.save(answerChat);

        // DTO 생성 후 반환
        return ChatConverter.toChatStartResponseDTO(session.getId(),record.getId(), answerChat);
    }

    @Override
    @Transactional
    public ChatMessageListDTO getChattingList(Long recordID){

        Record record = recordRepository.findById(recordID)
                .orElseThrow(() -> new IllegalArgumentException("Record not found: " + recordID));
        Session session = record.getSession();
        List<Chat> chatList = session.getChats();

        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy - MM - dd");

        List<ChatMessageResponseDTO> messages = chatList.stream()
                .map(c -> ChatMessageResponseDTO.builder()
                        .content(c.getMessage())
                        .role(c.getRole())
                        .messageTime(c.getSendAt().toLocalTime().format(timeFmt))
                        .messageDate(c.getSendAt().toLocalDate().format(dateFmt))
                        .evidences(c.getEvidences().stream()
                                .map(ev -> EvidenceResponseDTO.builder()
                                        .url(s3FileService.getGetPreSignedUrlByKey(ev.getS3Key(), 60))
                                        .filename(ev.getFilename())
                                        .type(ev.getType())
                                        .build()
                                ).toList())
                        .build())
                .toList();

        return ChatMessageListDTO.builder()
                .sessionId(session.getId())
                .messages(messages)
                .build();
    }

    @Override
    @Transactional
    public ChatAnswerDTO attachChatting(ChatWithEvidenceDTO chatWithEvidenceDTO){
        Long sessionId = chatWithEvidenceDTO.getChatSessionId();
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "세션이 존재하지 않습니다."));

        String text = chatWithEvidenceDTO.getText();
        Chat chatRequest = ChatConverter.requestToChat(ChatRequestDTO.builder().message(text).build(), session);

        List<EvidenceSaveRequestDTO> evidences = chatWithEvidenceDTO.getEvidences();

        // 증거 저장 부분
        if (evidences != null && !evidences.isEmpty()) {
            if (evidences.size() > 10) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "증거 파일은 최대 10개까지 첨부할 수 있습니다.");
            }
            for (EvidenceSaveRequestDTO it : evidences) {
                Evidence evidence = Evidence.builder()
                        .record(session.getRecord())
                        .type(RecordConverter.mapEvidenceType(it.getType()))
                        .filename(it.getFilename())
                        .s3Key(it.getS3Key())
                        .build();

                chatRequest.getEvidences().add(evidence);
            }
        }
        chatRepository.save(chatRequest);

        // AI 답변 생성
        String aiAnswer = aiService.ChatAnswer(chatRequest.getMessage());
        Chat answerChat = ChatConverter.anwerToChat(aiAnswer, session);

        chatRepository.save(answerChat);

        return ChatConverter.toChatMessageResponseDTO(aiAnswer);
    }
    @Override
    @Transactional
    public ChatEndResponseDTO endChatting(ChatEndRequestDTO chatEndRequestDTO){

        Long sessionId = chatEndRequestDTO.getChatSessionId();
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "세션이 존재하지 않습니다."));

        Record record = session.getRecord();

        StringBuilder sb = new StringBuilder();
        for (Chat chat : session.getChats()) {
            sb.append(chat.getRole())
                    .append(":")
                    .append(chat.getMessage())
                    .append("\n");
        }
        String chattingLogs = sb.toString();

        ChatSummaryDTO chatSummaryDTO = aiService.chatSummary(chattingLogs);
        return ChatEndResponseDTO.builder()
                .recordId(record.getId())
                .recordDetail(chatSummaryDTO)
                .build();
    }
}
