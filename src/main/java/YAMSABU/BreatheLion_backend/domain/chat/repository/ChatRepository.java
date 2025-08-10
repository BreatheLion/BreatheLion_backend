package YAMSABU.BreatheLion_backend.domain.chat.repository;

import YAMSABU.BreatheLion_backend.domain.chat.entity.ChatLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<ChatLog,Long> {
}
