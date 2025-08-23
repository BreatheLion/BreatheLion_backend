package YAMSABU.BreatheLion_backend.domain.record.repository;

import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.record.entity.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByDrawer(Drawer drawer);

    List<Record> findByRecordStatusOrderByCreatedAtDesc(RecordStatus status);
}
