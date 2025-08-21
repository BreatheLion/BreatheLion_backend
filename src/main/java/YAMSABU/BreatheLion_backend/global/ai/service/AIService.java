package YAMSABU.BreatheLion_backend.global.ai.service;

import YAMSABU.BreatheLion_backend.global.ai.dto.AIAnswerDTO.ChatSummaryDTO;
import YAMSABU.BreatheLion_backend.global.ai.dto.AIAnswerDTO.LawListDTO;
import YAMSABU.BreatheLion_backend.global.ai.dto.AIAnswerDTO.SOA_DTO;

import java.util.List;

public interface AIService {
    String ChatAnswer(String text);

    // 서랍 요약 기능 + 기관 선택 기능 + 행동 추천 기능
    SOA_DTO helpAnswer(String summaries);

    // 관련 법률
    LawListDTO lawSearch(String summaries);

    // 채팅 전문 요약
    ChatSummaryDTO chatSummary(String chattings);
}
