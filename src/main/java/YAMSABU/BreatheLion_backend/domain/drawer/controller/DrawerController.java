package YAMSABU.BreatheLion_backend.domain.drawer.controller;

import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.*;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerListResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerCreateRequestDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.AIHelpResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.service.DrawerService;
import YAMSABU.BreatheLion_backend.global.response.ApiResponse;
import YAMSABU.BreatheLion_backend.global.pdf.PdfServiceImpl;
import YAMSABU.BreatheLion_backend.global.pdf.PdfNoticeRequestDTO;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.record.entity.RecordStatus;
import YAMSABU.BreatheLion_backend.domain.record.repository.RecordRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/drawers")
public class DrawerController {
    private final DrawerService drawerService;
    private final PdfServiceImpl pdfServiceImpl;
    private final RecordRepository recordRepository;

    @PostMapping("/create/")
    public ApiResponse<DrawerResponseDTO> createDrawer(@Valid @RequestBody DrawerCreateRequestDTO drawerCreateRequest) {
        return ApiResponse.onSuccess("서랍 생성 성공", drawerService.createDrawer(drawerCreateRequest));
    }

    // 서랍 목록 조회
    @GetMapping("/list/")
    public ApiResponse<DrawerListResponseDTO> getDrawerList() {
        return ApiResponse.onSuccess("서랍 목록 조회 성공", drawerService.getDrawerList());
    }

    // 서랍 삭제
    @PatchMapping("/delete/")
    public ApiResponse<Void> deleteDrawer(@Valid @RequestBody DrawerDeleteRequestDTO drawerDeleteRequestDTO) {
        drawerService.deleteDrawers(drawerDeleteRequestDTO);
        return ApiResponse.onSuccess("서랍 삭제 성공");
    }

    private HttpHeaders pdfHeaders(String filename) {
        ContentDisposition cd = ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(cd);
        return headers;
    }

    // 전체 PDF 다운로드 (GET)
    @GetMapping("/{drawer_id}/pdf/")
    public ResponseEntity<byte[]> downloadAllPdf(@PathVariable("drawer_id") Long drawerId) {
        // 해당 서랍의 FINALIZED 기록 모두 조회 (오래된 순)
        List<Record> records = recordRepository.findAllForPdf(drawerId, RecordStatus.FINALIZED)
                .stream()
                .filter(r -> r.getDrawer() != null && r.getDrawer().getId().equals(drawerId))
                .sorted(Comparator.comparing(
                        Record::getOccurredAt,
                        java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())
                ))
                .toList();
        String drawerName = drawerService.getDrawerName(drawerId);
        byte[] pdfBytes = pdfServiceImpl.exportAllPdf(records, drawerName);
        HttpHeaders headers = pdfHeaders("timeline.pdf");
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    @GetMapping("/{drawer_id}/helpai/")
    public ApiResponse<AIHelpResponseDTO> helpAI(@PathVariable("drawer_id") Long drawerId){
        return ApiResponse.onSuccess("AI 도움 조회 성공", drawerService.helpAI(drawerId));
    }

    @GetMapping("/{drawer_id}/timeline/")
    public ApiResponse<TimelineListDTO> searchSummaryByKeyword(
            @PathVariable("drawer_id") Long drawerId, @RequestParam(value = "keyword", required = false) String keyword) {
        return ApiResponse.onSuccess("검색 결과", drawerService.searchSummaryByKeyword(drawerId, keyword));
    }
}
