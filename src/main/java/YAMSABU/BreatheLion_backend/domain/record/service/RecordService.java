package YAMSABU.BreatheLion_backend.domain.record.service;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO.*;

public interface RecordService {

    // 채팅 마치고 초안(DRAFT) 생성
    Long createDraft(RecordDraftRequestDTO request);

    // DRAFT -> FINALIZED
    void saveFinalize(RecordSaveRequestDTO request);

    // 최근 기록 목록
    RecordRecentResponseDTO getRecent();

    // 상세 기록 조회
    RecordDetailResponseDTO getDetail(Long recordId);

    // 상세 기록 삭제
    void deleteRecord(Long recordId);

    // 기록 제목 수정
    void updateTitle(Long recordId, String title);

    // 기록 폴더 수정
    void updateDrawer(Long recordId, Long drawerId, String newName);

    // 확인하고 삭제하면 될듯(중간 수정은 불가능하니까 삭제)
//    // DRAFT 상태에서 수정가능
//    void updateDraft(Long recordId, RecordDraftRequestDTO request);

}
