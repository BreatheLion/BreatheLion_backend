package YAMSABU.BreatheLion_backend.domain.record.repository;

import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.record.entity.RecordStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    // FINALIZED된 기록만 최근 기록에서 보여주도록 함
    Page<Record> findByRecordStatus(RecordStatus status, Pageable pageable);
    List<Record> findByDrawer(Drawer drawer);
}
