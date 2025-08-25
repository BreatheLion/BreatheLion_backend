package YAMSABU.BreatheLion_backend.global.ai.service;

import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.global.ai.dto.AIAnswerDTO.ChatSummaryDTO;

public interface AIService {
    // 채팅 하는거
    String ChatAnswer(String text);

    // 서랍 요약 기능 + 기관 선택 기능 + 행동 추천 기능
    void helpAnswer(Long drawerId,String summaries);

    // 관련 법률
    void lawSearch(Long drawerId, String summaries);

    // 채팅 전문 요약
    ChatSummaryDTO chatSummary(String chattings);

    // 레코드의 요약
    String recordSummary(Record record);
}
