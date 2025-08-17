package YAMSABU.BreatheLion_backend.domain.drawer.controller;

import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerListResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerCreateRequestDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.service.DrawerService;
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


}
