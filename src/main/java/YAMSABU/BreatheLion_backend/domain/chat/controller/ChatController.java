package YAMSABU.BreatheLion_backend.domain.chat.controller;

import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatLogDTO.ChatStartResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatLogDTO.ChatStartRequestDTO;
import YAMSABU.BreatheLion_backend.domain.chat.service.ChatService;
import YAMSABU.BreatheLion_backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private ChatService chatService;
    @PostMapping("/start")
    public ApiResponse<ChatStartResponseDTO> startChat(ChatStartRequestDTO chatStartRequestDTO){
        return ApiResponse.onSuccess("채팅성공", chatService.startChating(chatStartRequestDTO));
    }

}
