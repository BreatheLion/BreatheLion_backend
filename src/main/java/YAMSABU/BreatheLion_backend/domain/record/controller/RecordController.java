package YAMSABU.BreatheLion_backend.domain.record.controller;

import YAMSABU.BreatheLion_backend.domain.record.service.RecordService;
import YAMSABU.BreatheLion_backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO.*;
import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;

    // 1. 최종 기록 저장하기 버튼(FINALIZED 상태)
    @PatchMapping("/save")
    public ApiResponse<Void> save(@Valid @RequestBody RecordSaveRequestDTO request) {
        recordService.saveFinalize(request);
        return ApiResponse.onSuccess("서랍에 기록이 만들어졌어요.");
    }

    // 2. 최근 기록 목록
    @GetMapping("/recent")
    public ApiResponse<RecordRecentResponseDTO> recent() {
        return ApiResponse.onSuccess("최근기록목록", recordService.getRecent());
    }

    // 3. 상세 기록 조회
    @GetMapping("/{record_id}")
    public ApiResponse<RecordDetailResponseDTO> detail(@PathVariable("record_id") Long recordId) {
        return ApiResponse.onSuccess("상세기록조회", recordService.getDetail(recordId));
    }

    // 4. 상세 기록 삭제
    @DeleteMapping("/{record_id}/delete")
    public ApiResponse<?> delete(@PathVariable("record_id") Long recordId) {
        recordService.deleteRecord(recordId);
        return ApiResponse.onSuccess("기록이 삭제되었습니다.");
    }

    // 5. 기록 마치기 버튼 누르고 완전 저장하기 전(DRAFT 상태)
    @PatchMapping("/{record_id}/draft")
    public ApiResponse<?> updateDraft(@PathVariable("record_id") Long recordId,
                                      @RequestBody RecordDraftRequestDTO request) {
        recordService.updateDraft(recordId, request);
        return ApiResponse.onSuccess("기록 수정 성공", recordService.getDetail(recordId));
    }
}
