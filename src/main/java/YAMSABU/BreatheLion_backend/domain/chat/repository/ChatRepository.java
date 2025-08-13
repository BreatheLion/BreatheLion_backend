package YAMSABU.BreatheLion_backend.domain.chat.repository;

import YAMSABU.BreatheLion_backend.domain.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat,Long> {
    List<Chat> findAllBySession_IdOrderBySendAtAsc(Long sessionId);
}
