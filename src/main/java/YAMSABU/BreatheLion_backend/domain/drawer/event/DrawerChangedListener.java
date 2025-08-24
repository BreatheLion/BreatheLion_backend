package YAMSABU.BreatheLion_backend.domain.drawer.event;

import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.domain.drawer.repository.DrawerRepository;
import YAMSABU.BreatheLion_backend.domain.record.repository.RecordRepository;
import YAMSABU.BreatheLion_backend.global.ai.service.AIService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DrawerChangedListener {

    private final RecordRepository recordRepository;
    private final DrawerRepository drawerRepository;
    private final AIService aiService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDrawerChanged(DrawerChangedEvent event) {
        Drawer drawer = drawerRepository.findById(event.drawerId())
                .orElseThrow();
        // 서랍 내 모든 레코드 요약 합치기 (필요 시 길이 제한)
        List<Record> records = recordRepository.findByDrawer(drawer);
        String summaries = records.stream()
                .map(Record::getSummary)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .limit(500) // ✅ 방어: 너무 많으면 잘라내기(옵션)
                .collect(Collectors.joining("\n"));

        aiService.lawSearch(drawer, summaries);
        aiService.helpAnswer(drawer, summaries);
    }
}