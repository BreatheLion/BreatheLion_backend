package YAMSABU.BreatheLion_backend.domain.record.repository;

import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.record.entity.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    // 페이징 없이 전체 FINALIZED 기록을 최신순으로 반환
    List<Record> findByRecordStatusOrderByCreatedAtDesc(RecordStatus status);
}
