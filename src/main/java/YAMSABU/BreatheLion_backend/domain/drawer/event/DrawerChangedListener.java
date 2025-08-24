package YAMSABU.BreatheLion_backend.domain.drawer.event;

import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.domain.drawer.repository.DrawerRepository;
import YAMSABU.BreatheLion_backend.domain.record.repository.RecordRepository;
import YAMSABU.BreatheLion_backend.global.ai.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DrawerChangedListener {

    private final RecordRepository recordRepository;
    private final DrawerRepository drawerRepository;
    private final AIService aiService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDrawerChanged(DrawerChangedEvent event) {
        log.info("📢이벤트 발생했습니다! drawerId={}", event.drawerId());

        Drawer drawer = drawerRepository.findById(event.drawerId()).orElseThrow();
        List<Record> records = recordRepository.findByDrawer(drawer);
        String summaries = records.stream()
                .map(Record::getSummary)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining("\n"));

        aiService.lawSearch(event.drawerId(), summaries);
        aiService.helpAnswer(event.drawerId(), summaries);
    }
}