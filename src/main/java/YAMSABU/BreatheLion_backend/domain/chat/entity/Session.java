package YAMSABU.BreatheLion_backend.domain.chat.entity;

import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private Record record;

    @OneToMany(mappedBy = "Session", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Chat> chats = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime startedAt;

    @CreationTimestamp
    private LocalDateTime endedAt;
}
