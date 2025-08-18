package YAMSABU.BreatheLion_backend.domain.person.repository;

import YAMSABU.BreatheLion_backend.domain.person.entity.Evidence;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    List<Evidence> findByRecord(Record record);
    void deleteByRecord(Record record);
}
