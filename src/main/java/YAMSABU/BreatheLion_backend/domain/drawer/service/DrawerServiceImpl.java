package YAMSABU.BreatheLion_backend.domain.drawer.service;

import YAMSABU.BreatheLion_backend.domain.drawer.converter.DrawerConverter;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.AIHelpResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerListResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerCreateRequestDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.domain.drawer.repository.DrawerRepository;
import YAMSABU.BreatheLion_backend.domain.organization.repository.OrganizationRepository;
import YAMSABU.BreatheLion_backend.domain.person.entity.PersonRole;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.record.repository.RecordRepository;
import YAMSABU.BreatheLion_backend.global.ai.service.AIService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DrawerServiceImpl implements DrawerService {

    private final DrawerRepository drawerRepository;
    private final RecordRepository recordRepository;
    private final AIService aiService;
    private final OrganizationRepository organizationRepository;

    @Override
    @Transactional
    public DrawerResponseDTO createDrawer(DrawerCreateRequestDTO request) {
        if (drawerRepository.existsByName(request.getDrawerName())) {
            throw new IllegalArgumentException("이미 존재하는 서랍 이름입니다: " + request.getDrawerName());
        } // 컨트롤러에서 예외처리 코드 필요

        Drawer drawer = Drawer.builder()
                .name(request.getDrawerName())
                .recordCount(0L)
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
  
    @Override
    @Transactional
    public AIHelpResponseDTO helpAI(Long drawerId){
        Drawer drawer = drawerRepository.findById(drawerId)
                .orElseThrow(() -> new EntityNotFoundException("Drawer not found: " + drawerId));

        List<Record> records = recordRepository.findByDrawer(drawer);

        String mergedSummaries = records.stream()
                .map(Record::getSummary) // 각 Record에서 summary를 추출
                .collect(Collectors.joining("\n")); // 줄바꿈으로 연결

        // 가해자들
        List<String> assailants = records.stream()
                .flatMap(r -> r.getRecordPersons().stream())
                .filter(rp -> rp.getRole() == PersonRole.ASSAILANT)
                .map(rp -> rp.getPerson().getName())
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        return DrawerConverter.drawersToAiDTO(drawer,assailants);
    }

    public void rename(Long drawerId, String newName) {
        if(newName == null || newName.isBlank()) {
                throw new IllegalArgumentException("서랍 이름의 형식이 올바르지 않습니다.");
        }
        Drawer drawer = drawerRepository.findById(drawerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 서랍입니다." + drawerId));
        drawer.setName(newName.trim());
        drawerRepository.save(drawer);
    }
}
