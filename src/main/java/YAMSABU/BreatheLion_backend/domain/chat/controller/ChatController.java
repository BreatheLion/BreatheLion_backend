package YAMSABU.BreatheLion_backend.domain.chat.controller;

import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartRequestDTO;
import YAMSABU.BreatheLion_backend.domain.chat.service.ChatService;
import YAMSABU.BreatheLion_backend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/start")
    public ApiResponse<ChatStartResponseDTO> startChat(@Valid @RequestBody ChatStartRequestDTO chatStartRequestDTO){
        return ApiResponse.onSuccess("채팅성공", chatService.startChatting(chatStartRequestDTO));
    }
}
