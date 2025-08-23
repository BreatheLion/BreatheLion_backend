package YAMSABU.BreatheLion_backend.domain.drawer.service;

import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerCreateRequestDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerDeleteRequestDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerListResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.AIHelpResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerTimelineResponseDTO;
import java.util.List;


public interface DrawerService {
    DrawerResponseDTO createDrawer(DrawerCreateRequestDTO request);

    DrawerListResponseDTO getDrawerList();

    void deleteDrawers(DrawerDeleteRequestDTO drawerDeleteRequestDTO);

    String getDrawerName(Long drawerId);
  
    AIHelpResponseDTO helpAI(Long drawerId);
  
    void rename(Long drawerId, String newName);

    List<DrawerTimelineResponseDTO> searchSummaryByKeyword(Long drawerId, String keyword);
}
