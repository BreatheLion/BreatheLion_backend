package YAMSABU.BreatheLion_backend.domain.chat.controller;

import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatAnswerDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatRequestDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatEndRequestDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatEndResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatMessageListDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatWithEvidenceDTO;
import YAMSABU.BreatheLion_backend.domain.chat.dto.ChatDTO.ChatStartResponseDTO;
import YAMSABU.BreatheLion_backend.domain.chat.service.ChatService;
import YAMSABU.BreatheLion_backend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/start/")
    public ApiResponse<ChatStartResponseDTO> startChat(@Valid @RequestBody ChatRequestDTO chatRequestDTO){
        return ApiResponse.onSuccess("채팅성공", chatService.startChatting(chatRequestDTO));
    }

    @PostMapping("/attach/")
    public ApiResponse<ChatAnswerDTO> attachChat(@Valid @RequestBody ChatWithEvidenceDTO chatWithEvidenceDTO){
        return ApiResponse.onSuccess("저장 및 전송 성공", chatService.attachChatting(chatWithEvidenceDTO));
    }

    @PutMapping("/end/")
    public ApiResponse<ChatEndResponseDTO>  endChat(@Valid @RequestBody ChatEndRequestDTO chatEndRequestDTO ){
        return ApiResponse.onSuccess("채팅 종료",chatService.endChatting(chatEndRequestDTO));
    }

    @GetMapping("/{record_id}/list/")
    public ApiResponse<ChatMessageListDTO> getList(@PathVariable("record_id") long recordId){
        return ApiResponse.onSuccess("채팅 조회 성공!", chatService.getChattingList(recordId));
    }
}
