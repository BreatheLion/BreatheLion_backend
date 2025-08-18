package YAMSABU.BreatheLion_backend.domain.chat.controller;

import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatMessageListDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartRequestDTO;
import YAMSABU.BreatheLion_backend.domain.chat.service.ChatService;
import YAMSABU.BreatheLion_backend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;
    @PostMapping("/start/")
    public ApiResponse<ChatStartResponseDTO> startChat(@Valid @RequestBody ChatStartRequestDTO chatStartRequestDTO){
        return ApiResponse.onSuccess("채팅 성공", chatService.startChatting(chatStartRequestDTO));
    }

//    @GetMapping("/end/")
//    public ApiResponse<> endChat(){
//        return ApiResponse.onSuccess("채팅 끝",chatService.endChatting();
//    }

    @GetMapping("/{record_id}/list")
    public ApiResponse<ChatMessageListDTO> retrieveChat(@PathVariable("record_id") Long recordId) {
        return ApiResponse.onSuccess("채팅 조회 성공!", chatService.getChatList(recordId));
    }
}
