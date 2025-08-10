package YAMSABU.BreatheLion_backend.domain.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
@Tag(name = "Member", description = "회원 관련 API")
public class ChatController {

    @Operation(summary = "채팅 시작 버튼", description = "텍스트를 입력 받아 채팅을 시작합니다.")
    @PostMapping("/start")




}
