package YAMSABU.BreatheLion_backend.domain.drawer.controller;

import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerListResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerCreateRequestDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.AIHelpResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.service.DrawerService;
import YAMSABU.BreatheLion_backend.global.ai.dto.AIAnswerDTO;
import YAMSABU.BreatheLion_backend.global.ai.service.AIService;
import YAMSABU.BreatheLion_backend.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/drawers")
public class DrawerController {
    private final DrawerService drawerService;

    @PostMapping("/create/")
    public ApiResponse<DrawerResponseDTO> createDrawer(@Valid @RequestBody DrawerCreateRequestDTO drawerCreateRequest){
        return ApiResponse.onSuccess("서랍 생성 성공", drawerService.createDrawer(drawerCreateRequest));
    }

    @GetMapping("/list/")
    public ApiResponse<DrawerListResponseDTO> getDrawerList(){
        return ApiResponse.onSuccess("서랍 목록 조회 성공", drawerService.getDrawerList());
    }
    @DeleteMapping("/{drawer_id}/delete/")
    public ApiResponse<Void> deleteDrawer(@PathVariable("drawer_id") Long drawerId) {
        drawerService.deleteDrawer(drawerId);
        return ApiResponse.onSuccess("서랍 삭제 성공");
    }
    @GetMapping("/{drawer_id}/helpai/")
    public ApiResponse<AIHelpResponseDTO> helpAI(@PathVariable("drawer_id")Long drawerId){
        return ApiResponse.onSuccess("AI 도움 조회 성공", drawerService.helpAI(drawerId));
    }

    private final AIService aiService;
    @GetMapping("/ai")
    public ApiResponse<AIAnswerDTO.LawListDTO> getAIList(){
        return ApiResponse.onSuccess("잘되나",aiService.lawSearch(List.of(
                "온라인 쇼핑몰을 운영하던 판매자가 선입금을 받은 뒤 물건을 보내지 않고 잠적했습니다.",
                "회식 자리에서 상사가 부하 직원에게 폭행을 가해 상해 사건으로 이어졌습니다.",
                "아파트 주차장에서 차량을 절도당한 후 며칠 뒤 다른 지역에서 발견되었습니다.",
                "지인의 명의를 도용해 불법 대출을 받은 사실이 드러났습니다.",
                "편의점 야간 근무 중 괴한이 흉기를 들이밀고 현금을 강탈해 달아났습니다."
        )));
    }
}
