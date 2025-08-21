package YAMSABU.BreatheLion_backend.domain.drawer.service;

import YAMSABU.BreatheLion_backend.domain.drawer.converter.DrawerConverter;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerListResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerCreateRequestDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.domain.drawer.repository.DrawerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        Drawer drawer = Drawer.builder()
                .name(request.getDrawerName())
                .record_count(0L)
                .build();

        Drawer saved = drawerRepository.save(drawer);
        return DrawerConverter.drawerToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DrawerListResponseDTO getDrawerList() {
        List<Drawer> drawers = drawerRepository.findAllByOrderByCreatedAtDesc();
        return DrawerConverter.drawersToList(drawers);
    }

    @Override
    @Transactional
    public void deleteDrawer(Long drawerId) {
        drawerRepository.deleteById(drawerId);
    }

    @Override
    @Transactional(readOnly = true)
    public String getDrawerName(Long drawerId) {
        Drawer drawer = drawerRepository.findById(drawerId)
            .orElseThrow(() -> new IllegalArgumentException("서랍을 찾을 수 없습니다: " + drawerId));
        return drawer.getName();
    }


}
