package YAMSABU.BreatheLion_backend.chat.entity;

import YAMSABU.BreatheLion_backend.record.entity.Record;
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
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private Record record;

    @OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<ChatLog> chatLogs = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime startedAt;

    @CreationTimestamp
    private LocalDateTime endedAt;
}
