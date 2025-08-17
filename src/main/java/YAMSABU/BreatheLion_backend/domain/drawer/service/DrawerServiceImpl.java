package YAMSABU.BreatheLion_backend.domain.drawer.service;

import YAMSABU.BreatheLion_backend.domain.drawer.converter.DrawerConverter;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerCreateRequestDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.domain.drawer.repository.DrawerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DrawerServiceImpl implements DrawerService {

    private final DrawerRepository drawerRepository;

    @Override
    @Transactional
    public DrawerResponseDTO createDrawer(DrawerCreateRequestDTO request) {
         if (drawerRepository.existsByName(request.getDrawerName())) {
             throw new IllegalArgumentException("이미 존재하는 서랍 이름입니다: " + request.getDrawerName());
         } // 컨트롤러에서 예외처리 코드 필요

        Drawer drawer = Drawer.builder().name(request.getDrawerName()).build();
        drawerRepository.save(drawer);
        return DrawerConverter.DrawerToResponse(drawer);
    }
}
