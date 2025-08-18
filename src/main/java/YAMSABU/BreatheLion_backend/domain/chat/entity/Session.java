package YAMSABU.BreatheLion_backend.domain.chat.entity;

import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<Chat> chats = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    public Session(Record record){
        this.record = record;
    }
    public void endSession() {
        this.endedAt = LocalDateTime.now();
    }
}
