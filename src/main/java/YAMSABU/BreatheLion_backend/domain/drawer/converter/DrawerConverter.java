package YAMSABU.BreatheLion_backend.domain.drawer.converter;

import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;

import java.time.format.DateTimeFormatter;

public class DrawerConverter {
    public static DrawerResponseDTO DrawerToResponse(Drawer drawer){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return DrawerResponseDTO.builder()
                .drawerId(drawer.getId())
                .name(drawer.getName())
                .createdAt(drawer.getCreatedAt().format(formatter))
                .build();
    }
}
