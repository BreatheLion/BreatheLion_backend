package YAMSABU.BreatheLion_backend.domain.drawer.service;

import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerCreateRequestDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerListResponseDTO;


public interface DrawerService {
    DrawerResponseDTO createDrawer(DrawerCreateRequestDTO request);

    DrawerListResponseDTO getDrawerList();

    void deleteDrawer(Long drawerId);

    String getDrawerName(Long drawerId);
}
