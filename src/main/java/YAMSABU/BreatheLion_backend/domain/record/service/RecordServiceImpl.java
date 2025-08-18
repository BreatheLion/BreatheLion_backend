package YAMSABU.BreatheLion_backend.domain.record.service;

import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.domain.drawer.repository.DrawerRepository;
import YAMSABU.BreatheLion_backend.domain.person.entity.Evidence;
import YAMSABU.BreatheLion_backend.domain.person.entity.Person;
import YAMSABU.BreatheLion_backend.domain.person.entity.PersonRole;
import YAMSABU.BreatheLion_backend.domain.person.repository.EvidenceRepository;
import YAMSABU.BreatheLion_backend.domain.person.repository.PersonRepository;
import YAMSABU.BreatheLion_backend.domain.record.converter.RecordConverter;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.record.entity.RecordStatus;
import YAMSABU.BreatheLion_backend.domain.record.repository.RecordRepository;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO;
import YAMSABU.BreatheLion_backend.domain.record.dto.RecordDTO.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RecordServiceImpl implements RecordService{

    private final RecordRepository recordRepository;
    private final DrawerRepository drawerRepository;
    private final PersonRepository personRepository;
    private final EvidenceRepository evidenceRepository;
//    private final S3Port s3Service;

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
        if (request.getSeverity() == null ||request.getSeverity() < 1 || request.getSeverity() > 5)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "severity는 1~5여야 합니다.");
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

        evidenceRepository.deleteByRecord(record);
        if (request.getEvidences() != null) {
            for (var it : request.getEvidences()) {
                Evidence evidence = Evidence.builder()
                        .record(record)
                        .type(RecordConverter.mapEvidenceType(it.getType()))
                        .filename(it.getFilename())
                        .s3Key(it.getS3Key())
                        .build();
                evidenceRepository.save(evidence);
            }
        }

        record.setRecordStatus(RecordStatus.FINALIZED);
        recordRepository.save(record);
    }

    // 한 페이지당 몇개씩 기록 보여주는 지 몰라서 15개로 일단 해놓음
    @Transactional(readOnly = true)
    @Override
    public RecordRecentResponseDTO getRecent() {
        var page = PageRequest.of(0, 15, Sort.by(Sort.Direction.DESC, "createdAt"));
        var items = recordRepository.findByRecordStatus(RecordStatus.FINALIZED, page)
                .getContent()
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
//                        .s3Url(s3Service.generatePresignedGetUrl(evidence.getS3Key(), 10)) // 10분 유효시간
                        .s3Url(null) //  일단 이거 넣어둠
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
    @Transactional
    public void updateDraft(Long recordId, RecordDraftRequestDTO request) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 기록은 존재하지 않습니다."));

        if (record.getRecordStatus() != RecordStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 저장된 기록은 수정할 수 없습니다.");
        }
        if (request.getContent() != null)
            record.setContent(request.getContent());
        if(request.getTitle() != null)
            record.setTitle(request.getTitle());
        if (request.getSeverity() != null) {
            if(request.getSeverity() < 1 || request.getSeverity() > 5) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "severity는 1~5여야 합니다.");
            }
            record.setSeverity(request.getSeverity());
        }

        if (request.getLocation() != null)
            record.setLocation(request.getLocation());
        if (request.getOccurredAt() != null)
            record.setOccurredAt(request.getOccurredAt());
        if (request.getCategories() != null) {
            record.getCategories().clear();
            record.getCategories().addAll(RecordConverter.mapCategories(request.getCategories()));
        }
        if (request.getDrawer() != null && !request.getDrawer().isBlank()) {
            Drawer drawer = drawerRepository.findByName(request.getDrawer())
                    .orElseGet(() -> drawerRepository.save(Drawer.builder()
                            .name(request.getDrawer()).build()));
            record.setDrawer(drawer);
        }
        if (request.getAssailant() != null || request.getWitness() != null) {
            record.clear();
            attachPeople(record, splitNames(request.getAssailant()), PersonRole.ASSAILANT);
            attachPeople(record, splitNames(request.getWitness()), PersonRole.WITNESS);
        }

        if (request.getEvidences() != null) {
            evidenceRepository.deleteByRecord(record);
            for (var it : request.getEvidences()) {
                Evidence evidence = Evidence.builder()
                        .record(record)
                        .type(RecordConverter.mapEvidenceType(it.getType()))
                        .filename(it.getFilename())
                        .s3Key(it.getS3Key())
                        .build();

                evidenceRepository.save(evidence);
            }
        }

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
}
