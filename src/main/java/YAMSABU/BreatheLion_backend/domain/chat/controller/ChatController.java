package YAMSABU.BreatheLion_backend.domain.chat.controller;

import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatWithEvidenceDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatMessageResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.service.ChatService;
import YAMSABU.BreatheLion_backend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/start/")
    public ApiResponse<ChatStartResponseDTO> startChat(@Valid @RequestBody ChatDTO.ChatRequestDTO chatRequestDTO){
        return ApiResponse.onSuccess("채팅성공", chatService.startChatting(chatRequestDTO));
    }

    @PostMapping("/attach/")
    public ApiResponse<ChatMessageResponseDTO> attachChat(@Valid @RequestBody ChatWithEvidenceDTO chatWithEvidenceDTO){
        return ApiResponse.onSuccess("저장 및 전송 성공", chatService.attachChatting(chatWithEvidenceDTO));
    }

    @GetMapping("/{record_id}/list/")
}
