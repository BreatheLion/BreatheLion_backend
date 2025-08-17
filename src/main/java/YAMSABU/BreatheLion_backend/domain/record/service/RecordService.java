package YAMSABU.BreatheLion_backend.domain.record.service;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO.*;

public interface RecordService {
//    // DRAFT
//    void updateDraft(Long recordId, RecordDraftRequestDTO request);
//
//    // 최종 저장(FINALIZE) – PATCH /api/records/save
//    void saveFinalize(SaveRecordRequest request);
//
//    // 조회
//    List<RecentRecordResponse> getRecent();
//
//    RecordDetailResponseDTO getDetail(Long recordId);
//
//    // 삭제
//    void deleteRecord(Long recordId);
// DRAFT -> FINALIZED
    RecordSaveResponseDTO saveFinalize(RecordSaveRequestDTO request);

    // 최근 기록 목록
    RecordRecentResponseDTO getRecent();

    // 상세 기록 조회
    RecordDetailResponseDTO getDetail(Long recordId);

    // 상세 기록 삭제
    void deleteRecord(Long recordId);

    // DRAFT 상태에서 수정가능
    void updateDraft(Long recordId, RecordDTO.RecordDraftRequestDTO request);

}
