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
import YAMSABU.BreatheLion_backend.domain.person.entity.PersonRole;
import YAMSABU.BreatheLion_backend.domain.record.converter.RecordConverter;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO.TimelineResponseDTO;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.record.repository.RecordRepository;
import YAMSABU.BreatheLion_backend.global.code.GlobalErrorCode;
import YAMSABU.BreatheLion_backend.global.exception.CustomException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DrawerServiceImpl implements DrawerService {

    private final DrawerRepository drawerRepository;
    private final RecordRepository recordRepository;

    @Override
    @Transactional
    public DrawerResponseDTO createDrawer(DrawerCreateRequestDTO request) {
        if (drawerRepository.existsByName(request.getDrawerName())) {
            throw new CustomException(GlobalErrorCode.DRAWER_ALREADY_EXISTS);
        }
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
        if (dto == null || dto.getDeleteList() == null || dto.getDeleteList().isEmpty()) {
            throw new CustomException(GlobalErrorCode._INVALID_PARAMETER, "삭제 대상이 없습니다.");
        }

        for (Long drawerId : dto.getDeleteList()) {
            Drawer drawer = drawerRepository.findById(drawerId)
                    .orElseThrow(() -> new CustomException(GlobalErrorCode.DRAWER_NOT_FOUND));

            List<Record> records = recordRepository.findByDrawerId(drawerId);
            try {
                if (!records.isEmpty()) {
                    recordRepository.deleteAll(records);
                }
                drawerRepository.delete(drawer);
            } catch (Exception e) { // 하나라도 실패하면 전체 롤백
                throw new CustomException(GlobalErrorCode.DRAWER_DELETE_FAILED);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public String getDrawerName(Long drawerId) {
        Drawer drawer = drawerRepository.findById(drawerId)
                .orElseThrow(() -> new CustomException(GlobalErrorCode.DRAWER_NOT_FOUND));
        return drawer.getName();
    }

    @Override
    @Transactional
    public AIHelpResponseDTO helpAI(Long drawerId){
        Drawer drawer = drawerRepository.findById(drawerId)
                .orElseThrow(() -> new CustomException(GlobalErrorCode.DRAWER_NOT_FOUND));

        List<Record> records = recordRepository.findByDrawer(drawer);

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
            throw new CustomException(GlobalErrorCode.DRAWER_INVALID_NAME);
        }
        Drawer drawer = drawerRepository.findById(drawerId)
                .orElseThrow(() -> new CustomException(GlobalErrorCode.DRAWER_NOT_FOUND));
        drawer.setName(newName.trim());
        drawerRepository.save(drawer);
    }

    @Override
    @Transactional(readOnly = true)
    public TimelineListDTO searchSummaryByKeyword(Long drawerId, String keyword) {
        Drawer drawer = drawerRepository.findById(drawerId)
                .orElseThrow(() -> new CustomException(GlobalErrorCode.DRAWER_NOT_FOUND));

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