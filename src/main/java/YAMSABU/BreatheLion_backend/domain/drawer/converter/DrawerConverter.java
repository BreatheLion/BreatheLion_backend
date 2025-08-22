package YAMSABU.BreatheLion_backend.domain.drawer.converter;

import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.AIHelpResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerListResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerItemDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.global.ai.dto.AIAnswerDTO.SOA_DTO;
import YAMSABU.BreatheLion_backend.global.ai.dto.AIAnswerDTO.LawListDTO;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
                .recordCount(drawer.getRecord_count())
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

    public static AIHelpResponseDTO drawersToAiDTO(Drawer drawer, List<String> assailants, SOA_DTO soaDto,LawListDTO laws) {
        return null;
    }

}
