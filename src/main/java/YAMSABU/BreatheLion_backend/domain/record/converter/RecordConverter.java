package YAMSABU.BreatheLion_backend.domain.record.converter;

import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.domain.person.entity.PersonRole;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO.RecordDetailResponseDTO;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO.RecordRecentItemDTO;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.evidence.entity.EvidenceType;
import YAMSABU.BreatheLion_backend.domain.record.entity.RecordCategory;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO.TimelineResponseDTO;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RecordConverter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd (HH : mm)");

    // 최근 기록 확인(조회) 리스트
        // 년. 월. 일. (시 : 분)
    public static RecordRecentItemDTO toRecentItem(Record record) {
        Drawer drawer = record.getDrawer();
        return RecordRecentItemDTO.builder()
                .recordId(record.getId())
                .drawerId(drawer.getId())
                .drawer_title(drawer.getName())
                .record_title(record.getTitle())
                .location(record.getLocation())
                .assailant(extractNamesByRole(record, PersonRole.ASSAILANT))
                .createdAt(record.getCreatedAt())
                .build();
    }

    public static RecordDetailResponseDTO toDetail(Record record, List<RecordDetailResponseDTO.EvidenceItemDTO> evidences) {
        return RecordDetailResponseDTO.builder()
                .recordId(record.getId())
                .drawerId(record.getDrawer().getId())
                .drawer(record.getDrawer().getName())
                .category(record.getCategory().getLabel())
                .title(record.getTitle())
                .content(record.getContent())
                .severity(record.getSeverity())
                .location(record.getLocation())
                .occurredAt(record.getOccurredAt())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .assailant(extractNamesByRole(record, PersonRole.ASSAILANT))
                .witness(extractNamesByRole(record, PersonRole.WITNESS))
                .evidences(evidences)
                .build();
    }

    public static EvidenceType mapEvidenceType(String raw) {
        if (raw == null) return EvidenceType.FILE;
        try {
            return EvidenceType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return EvidenceType.FILE;
        }
    }

    public static RecordCategory mapCategory(String raw) {
        if (raw == null) return null;
        String normalized = java.text.Normalizer.normalize(raw.trim(), java.text.Normalizer.Form.NFKC);
        if (normalized.isEmpty()) return null;
        return RecordCategory.fromLabel(normalized);
    }

    public static List<String> extractNamesByRole(Record record, PersonRole role) {
        if (record.getRecordPersons() == null) {
            return Collections.emptyList();
        }
        return record.getRecordPersons().stream()
                .filter(recordPerson -> recordPerson.getRole() == role)
                .map(recordPerson -> recordPerson.getPerson().getName())
                .collect(Collectors.toList());
    }
    public static TimelineResponseDTO toTimelineResponseDTO(Record record) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd.");

        return TimelineResponseDTO.builder()
                .recordId(record.getId())
                .category(record.getCategory().getLabel())
                .title(record.getTitle())
                .severity(record.getSeverity())
                .summary(record.getSummary())
                .occurredAt(record.getOccurredAt().format(formatter))
                .build();
    }
}

