package YAMSABU.BreatheLion_backend.domain.drawer.converter;

import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO.*;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.*;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.AIHelpResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerListResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerItemDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.OrganizationDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.drawer.entity.Law;
import YAMSABU.BreatheLion_backend.domain.organization.entity.Organization;
import YAMSABU.BreatheLion_backend.global.ai.dto.AIAnswerDTO.LawDTO;
import YAMSABU.BreatheLion_backend.global.ai.dto.AIAnswerDTO.LawListDTO;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class DrawerConverter {
    // 년. 월. 일. (시 : 분)
    public static DrawerResponseDTO drawerToResponse(Drawer drawer){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd (HH : mm)");

        return DrawerResponseDTO.builder()
                .drawerId(drawer.getId())
                .name(drawer.getName())
                .createdAt(drawer.getCreatedAt().format(formatter))
                .build();
    }
    // 년. 월. 일. (시 : 분)
    public static DrawerItemDTO toItemDTO(Drawer drawer) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd (HH : mm)");

        return DrawerItemDTO.builder()
                .drawerId(drawer.getId())
                .name(drawer.getName())
                .recordCount(drawer.getRecordCount())
                .createdAt(drawer.getCreatedAt().format(formatter))
                .updatedAt(drawer.getUpdatedAt().format(formatter))
                .build();
    }

    public static DrawerListResponseDTO drawersToList(List<Drawer> drawers) {
        List<DrawerItemDTO> drawerDTOs = drawers.stream()
                .map(DrawerConverter::toItemDTO)
                .collect(Collectors.toList());

        return DrawerListResponseDTO.builder()
                .drawers(drawerDTOs)
                .build();
    }

    public static AIHelpResponseDTO drawersToAiDTO(Drawer drawer, List<String> assailants) {
        List<LawDTO> lawDTOs = (drawer.getRelatedLaws() == null ? List.<Law>of() : drawer.getRelatedLaws())
                .stream()
                .map(law -> LawDTO.builder()
                        .lawName(law.getLawName())
                        .article(law.getArticle())
                        .content(law.getContent())
                        .build())
                .toList();

        LawListDTO lawListDTO = LawListDTO.builder()
                .laws(lawDTOs)
                .build();

        List<OrganizationDTO> orgDTOs = (drawer.getOrganizations() == null ? Set.<Organization>of() : drawer.getOrganizations())
                .stream()
                .map(org -> OrganizationDTO.builder()
                        .name(org.getName())
                        .phone(org.getPhone())
                        .url(org.getUrl())
                        .description(org.getDescription())
                        .build())
                .toList();

        return AIHelpResponseDTO.builder()
                .drawerName(drawer.getName())
                .assailant(assailants == null ? List.of() : assailants)   // 그대로 사용
                .recordCount(drawer.getRecordCount() == null ? 0L : drawer.getRecordCount())
                .summary(drawer.getSummary())
                .careGuide(drawer.getAction())
                .relatedLaws(lawListDTO)
                .organizations(orgDTOs)
                .build();
    }

    public static DrawerDTO.DrawerTimelineResponseDTO toTimelineResponseDTO(Record record) {
        return DrawerDTO.DrawerTimelineResponseDTO.builder()
                .recordId(record.getId())
                .category(record.getCategory() == null ? null : record.getCategory().name())
                .title(record.getTitle())
                .severity(record.getSeverity())
                .location(record.getLocation())
                .summary(record.getSummary())
                .occurredAt(record.getOccurredAt() == null ? null : record.getOccurredAt().toString())
                .build();
    }
}
