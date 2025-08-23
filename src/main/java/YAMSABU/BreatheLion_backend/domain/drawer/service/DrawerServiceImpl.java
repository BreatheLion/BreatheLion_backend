package YAMSABU.BreatheLion_backend.domain.drawer.service;

import YAMSABU.BreatheLion_backend.domain.drawer.converter.DrawerConverter;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.AIHelpResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerDeleteRequestDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerListResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerCreateRequestDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.DrawerResponseDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.TimelineListDTO;
import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.domain.drawer.repository.DrawerRepository;
import YAMSABU.BreatheLion_backend.domain.organization.repository.OrganizationRepository;
import YAMSABU.BreatheLion_backend.domain.person.entity.PersonRole;
import YAMSABU.BreatheLion_backend.domain.record.converter.RecordConverter;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO.TimelineResponseDTO;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.record.repository.RecordRepository;
import YAMSABU.BreatheLion_backend.global.ai.service.AIService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
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
    public void deleteDrawers(DrawerDeleteRequestDTO dto) {
        for (Long drawerId : dto.getDeleteList()) {
            Drawer drawer = drawerRepository.findById(drawerId)
                    .orElseThrow(() -> new EntityNotFoundException("Drawer not found: " + drawerId));

            List<Record> records = recordRepository.findByDrawerId(drawerId);

            if (!records.isEmpty()) {
                recordRepository.deleteAll(records);
            }
            drawerRepository.delete(drawer);
        }
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

    @Override
    @Transactional(readOnly = true)
    public TimelineListDTO searchSummaryByKeyword(Long drawerId, String keyword) {
        Drawer drawer = drawerRepository.findById(drawerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 서랍입니다." + drawerId));

        // 키워드 정규화 (null/공백 → null, LIKE 특수문자 escape)
        String kw = normalizeKeyword(keyword);

        // 분기: 전체 vs 키워드 검색
        List<Record> records = (kw == null)
                ? recordRepository.findAllByDrawer(drawer.getId())
                : recordRepository.searchByDrawerAndKeyword(drawer.getId(), kw);

        // 결과 담을 리스트
        List<TimelineResponseDTO> timelines = new ArrayList<>();

        for (Record record : records) {
            TimelineResponseDTO dto = RecordConverter.toTimelineResponseDTO(record);
            timelines.add(dto);
        }
        return TimelineListDTO.builder()
                .timelines(timelines)
                .build();
    }
    // 전처리 작업
    private String normalizeKeyword(String keyword) {
        if (keyword == null) return null;
        String t = keyword.trim();
        if (t.isEmpty()) return null;
        return escapeForLike(t);
    }

    // 특수기호를 사용하기 위해 sql 특수기호 매핑
    private String escapeForLike(String s) {
        s = s.replace("!", "!!");
        s = s.replace("%", "!%");
        s = s.replace("_", "!_");
        return s;
    }
}
