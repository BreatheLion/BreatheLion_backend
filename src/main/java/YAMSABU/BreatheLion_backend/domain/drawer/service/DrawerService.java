package YAMSABU.BreatheLion_backend.domain.drawer.service;

import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerCreateRequestDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerListResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.AIHelpResponseDTO;


public interface DrawerService {
    DrawerResponseDTO createDrawer(DrawerCreateRequestDTO request);

    DrawerListResponseDTO getDrawerList();

    void deleteDrawer(Long drawerId);

    String getDrawerName(Long drawerId);
  
    AIHelpResponseDTO helpAI(Long drawerId);
  
    void rename(Long drawerId, String newName);
}
