package YAMSABU.BreatheLion_backend.domain.record.service;

import YAMSABU.BreatheLion_backend.domain.drawer.service.DrawerService;
import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.domain.drawer.repository.DrawerRepository;
import YAMSABU.BreatheLion_backend.domain.evidence.entity.Evidence;
import YAMSABU.BreatheLion_backend.domain.person.entity.Person;
import YAMSABU.BreatheLion_backend.domain.person.entity.PersonRole;
import YAMSABU.BreatheLion_backend.domain.evidence.repository.EvidenceRepository;
import YAMSABU.BreatheLion_backend.domain.person.repository.PersonRepository;
import YAMSABU.BreatheLion_backend.domain.record.converter.RecordConverter;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.record.entity.RecordStatus;
import YAMSABU.BreatheLion_backend.domain.record.repository.RecordRepository;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO.*;
import YAMSABU.BreatheLion_backend.global.s3.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import YAMSABU.BreatheLion_backend.domain.drawer.dto.DrawerDTO.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;
    private final DrawerRepository drawerRepository;
    private final PersonRepository personRepository;
    private final EvidenceRepository evidenceRepository;
    private final S3FileService s3FileService;
    private final DrawerService drawerService;

    @Override
    public void saveFinalize(RecordSaveRequestDTO request) {
        if (request.getRecordId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "record_id 필요");
        }
        Record record = recordRepository.findById(request.getRecordId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 기록은 존재하지 않습니다."));

        if (record.getRecordStatus() != RecordStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 저장한 기록은 수정할 수 없습니다.");
        }

        if (request.getContent() == null || request.getContent().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용은 필수항목입니다.");
        if (request.getLocation() == null || request.getLocation().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "장소는 필수항목입니다.");
        if (request.getOccurredAt() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "발생 시간은 필수항목입니다.");
        if (request.getSeverity() == null ||request.getSeverity() < 0 || request.getSeverity() > 2)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "severity는 0~2여야 합니다.");
        if(request.getTitle() != null && !request.getTitle().isBlank())
            record.setTitle(request.getTitle());

        record.setContent(request.getContent());
        record.setSeverity(request.getSeverity());
        record.setLocation(request.getLocation());
        record.setOccurredAt(request.getOccurredAt());

        if (request.getCategories() != null) {
            record.getCategories().clear();
            record.getCategories().addAll(RecordConverter.mapCategories(request.getCategories()));
        }

        if (request.getDrawer() != null && !request.getDrawer().isBlank()) {
            Drawer drawer = drawerRepository.findByName(request.getDrawer())
                    .orElseGet(() -> drawerRepository.save(Drawer.builder().name(request.getDrawer()).build()));
            record.setDrawer(drawer);
        }

        // 인물 처리
        record.clear();
        attachPeople(record, splitNames(request.getAssailant()), PersonRole.ASSAILANT);
        attachPeople(record, splitNames(request.getWitness()), PersonRole.WITNESS);

        // 증거파일은 채팅 중에만 저장, 이후에는 추가/삭제 불가

        record.setRecordStatus(RecordStatus.FINALIZED);
        recordRepository.save(record);
    }

    @Transactional(readOnly = true)
    @Override
    public RecordRecentResponseDTO getRecent() {
        var items = recordRepository.findByRecordStatusOrderByCreatedAtDesc(RecordStatus.FINALIZED)
                .stream()
                .map(RecordConverter::toRecentItem)
                .collect(Collectors.toList());
        return RecordRecentResponseDTO.builder()
                .records(items).build();
    }

    @Transactional(readOnly = true)
    @Override
    public RecordDetailResponseDTO getDetail(Long recordId) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 기록은 존재하지 않습니다."));

        List<Evidence> evidences = evidenceRepository.findByRecord(record);
        List<RecordDetailResponseDTO.EvidenceItemDTO> evidenceItemDTOS = evidences.stream()
                .map(evidence -> RecordDetailResponseDTO.EvidenceItemDTO.builder()
                        .id(evidence.getId())
                        .recordId(record.getId())
                        .type(evidence.getType().name())
                        .filename(evidence.getFilename())
                        .s3Url(s3FileService.getGetPreSignedUrlByKey(evidence.getS3Key(), 10)) // 10분 유효시간
                        .uploadedAt(evidence.getUploadedAt())
                        .build())
                .collect(Collectors.toList());

        return RecordConverter.toDetail(record, evidenceItemDTOS);
    }

    @Override
    public void deleteRecord(Long recordId) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 기록은 존재하지 않습니다."));
        evidenceRepository.deleteByRecord(record);
        recordRepository.delete(record);
    }

    @Override
    public void updateTitle(Long recordId, String title) {
        if (!StringUtils.hasText(title) || title.trim().length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "제목 형식이 올바르지 않습니다.");
        }
        // FINALIZED에서만 허용
        Record record = mustBeFinalized(recordId);
        record.setTitle(title.trim());
        recordRepository.save(record);
    }

    @Override
    public void updateDrawer(Long recordId, Long drawerId) {
        Record record = mustBeFinalized(recordId);
        Drawer drawer = drawerRepository.findById(drawerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 서랍은 존재하지 않습니다."));
        record.setDrawer(drawer);
        recordRepository.save(record);
    }


    private void attachPeople(Record record, List<String> names, PersonRole role) {
        for (String name : names) {
            Person p = personRepository.findByName(name)
                    .orElseGet(() -> personRepository.save(Person.builder()
                            .name(name)
                            .build()));
            record.add(p, role);
        }
    }

    private List<String> splitNames(List<String> rawList) {
        if (rawList == null)
            return new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (String s : rawList) {
            if (s == null) continue;
            for (String part : s.split("[,]")) {
                String n = part.trim();
                if (!n.isEmpty())
                    names.add(n);
            }
        }
        return names;
    }

    private Record mustBeFinalized(Long id) {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 기록은 존재하지 않습니다."));
        if(record.getRecordStatus() != RecordStatus.FINALIZED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "FINALIZED 상태에서만 수정이 가능합니다.");
        }
        return record;
    }

    @Override
    public Record getRecordEntity(Long recordId) {
        return recordRepository.findById(recordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "기록을 찾을 수 없습니다."));
    }
}
