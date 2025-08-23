package YAMSABU.BreatheLion_backend.domain.record.controller;

import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO.RecordDetailResponseDTO;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO.RecordDrawerUpdateRequestDTO;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO.RecordRecentResponseDTO;
import YAMSABU.BreatheLion_backend.domain.record.service.RecordService;
import YAMSABU.BreatheLion_backend.global.pdf.PdfExportService;
import YAMSABU.BreatheLion_backend.global.pdf.PdfNoticeRequestDTO;
import YAMSABU.BreatheLion_backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.List;

import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import org.springframework.http.ContentDisposition;

import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
@CrossOrigin
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;
    private final PdfExportService pdfExportService;

    // 2. 최종 기록 저장하기 버튼(FINALIZED 상태)
    @PatchMapping("/save/")
    public ApiResponse<Void> save(@Valid @RequestBody RecordDTO.RecordSaveRequestDTO request) {
        recordService.saveFinalize(request);
        return ApiResponse.onSuccess("서랍에 기록이 만들어졌어요.");
    }

    // 3. 최근 기록 목록
    @GetMapping("/recent/")
    public ApiResponse<RecordRecentResponseDTO> recent() {
        return ApiResponse.onSuccess("최근기록목록", recordService.getRecent());
    }

    // 4. 상세 기록 조회
    @GetMapping("/{record_id}/")
    public ApiResponse<RecordDetailResponseDTO> detail(@PathVariable("record_id") Long recordId) {
        return ApiResponse.onSuccess("상세기록조회", recordService.getDetail(recordId));
    }

    // 5. 상세 기록 삭제
    @DeleteMapping("/{record_id}/delete/")
    public ApiResponse<?> delete(@PathVariable("record_id") Long recordId) {
        recordService.deleteRecord(recordId);
        return ApiResponse.onSuccess("기록이 삭제되었습니다.");
    }

    @PatchMapping("/{record_id}/title/")
    public ApiResponse<Void> updateTitle(@PathVariable("record_id") Long recordId, @Valid @RequestBody RecordDTO.RecordTitleUpdateRequestDTO request) {
        recordService.updateTitle(recordId, request.getTitle());
        return ApiResponse.onSuccess("제목 수정 완료");
    }

    @PatchMapping("/{record_id}/drawer/")
    public ApiResponse<Void> updateDrawer(@PathVariable("record_id") Long recordId, @RequestBody RecordDrawerUpdateRequestDTO request) {
        recordService.updateDrawer(recordId, request.getDrawerId());
        return ApiResponse.onSuccess("폴더 이동 완료");
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

    @GetMapping("/{record_id}/pdf")
    public ResponseEntity<byte[]> downloadRecordConsultPdf(@PathVariable("record_id") Long recordId,
                                                          @RequestParam("type") String type) {
        if (!"consult".equals(type)) {
            return ResponseEntity.badRequest().body(null);
        }
        Record record = recordService.getRecordEntity(recordId);
        byte[] pdfBytes = pdfExportService.exportConsultPdf(List.of(record));
        HttpHeaders headers = pdfHeaders("consult.pdf");
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

    @PostMapping("/{record_id}/pdf/")
    public ResponseEntity<byte[]> downloadRecordNoticePdf(@PathVariable("record_id") Long recordId,
                                                         @RequestParam("type") String type,
                                                         @RequestBody PdfNoticeRequestDTO dto) {
        if (!"notice".equals(type)) {
            return ResponseEntity.badRequest().body(null);
        }
        Record record = recordService.getRecordEntity(recordId);
        byte[] pdfBytes = pdfExportService.exportNoticePdf(List.of(record), dto);
        HttpHeaders headers = pdfHeaders("notice.pdf");
        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }

}

