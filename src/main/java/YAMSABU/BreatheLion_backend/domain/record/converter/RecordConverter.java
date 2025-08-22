package YAMSABU.BreatheLion_backend.domain.record.converter;

import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.domain.person.entity.PersonRole;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.evidence.entity.EvidenceType;
import YAMSABU.BreatheLion_backend.domain.record.entity.RecordCategory;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO.*;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RecordConverter {

        // 최근 기록 확인(조회) 리스트
        // 년. 월. 일. (시 : 분)
    public static RecordRecentItemDTO toRecentItem(Record record) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd (HH : mm)");

        return RecordRecentItemDTO.builder()
                .recordId(record.getId())
                .drawerId(record.getDrawer() != null ? record.getDrawer().getId() : null)
                .drawer(record.getDrawer() != null ? record.getDrawer().getName() : null)
                .location(record.getLocation())
                .assailant(extractNamesByRole(record, PersonRole.ASSAILANT))
                .createdAt(record.getCreatedAt())
                .summary(record.getSummary())
                .build();
    }

    // 상세 응답 변환
    // 년. 월. 일. (시 : 분)
    public static RecordDetailResponseDTO toDetail(Record record, List<RecordDetailResponseDTO.EvidenceItemDTO> evidences) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd (HH : mm)");
        return RecordDetailResponseDTO.builder()
                .recordId(record.getId())
                .drawerId(record.getDrawer() != null ? record.getDrawer().getId() : null)
                .drawer(record.getDrawer() != null ? record.getDrawer().getName() : null)
                .categories(record.getCategories() != null ? record.getCategories().stream()
                        .map(RecordCategory::getLabel)
                        .collect(Collectors.toList()) : Collections.emptyList())
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

    public static Set<RecordCategory> mapCategories(List<String> rawList) {
        if (rawList == null) return Collections.emptySet();
        return rawList.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(s -> java.text.Normalizer.normalize(s.trim(), java.text.Normalizer.Form.NFKC))
                .map(RecordCategory::fromLabel)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
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
}
