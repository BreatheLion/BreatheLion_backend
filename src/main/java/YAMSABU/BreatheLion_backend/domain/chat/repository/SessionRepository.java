package YAMSABU.BreatheLion_backend.domain.chat.repository;

import YAMSABU.BreatheLion_backend.domain.chat.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session,Long> {
}
