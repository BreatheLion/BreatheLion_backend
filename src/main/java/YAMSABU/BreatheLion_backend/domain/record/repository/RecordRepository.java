package YAMSABU.BreatheLion_backend.domain.record.repository;

import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.record.entity.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByDrawer(Drawer drawer);

    // 페이징 없이 전체 FINALIZED 기록을 최신순으로 반환
    List<Record> findByRecordStatusOrderByCreatedAtDesc(RecordStatus status);

    // summary에 키워드가 포함된 FINALIZED 기록을 최신순으로 반환
    List<Record> findByRecordStatusAndSummaryContainingOrderByCreatedAtDesc(RecordStatus status, String keyword);

    // 특정 서랍장 내 FINALIZED 기록을 최신순으로 반환
    List<Record> findByDrawerAndRecordStatusOrderByCreatedAtDesc(Drawer drawer, RecordStatus status);

    // 특정 서랍장 내 summary에 키워드가 포함된 FINALIZED 기록을 최신순으로 반환
    List<Record> findByDrawerAndRecordStatusAndSummaryContainingOrderByCreatedAtDesc(Drawer drawer, RecordStatus status, String keyword);

    @Query("""
        select distinct r
        from Record r
        left join fetch r.recordPersons rp
        left join fetch rp.person p
        where r.drawer.id = :drawerId and r.recordStatus = :status
        """)
    List<Record> findAllForPdf(@Param("drawerId") Long drawerId,
                               @Param("status") RecordStatus status);

    List<Record> findByDrawerId(Long drawerId);
}
