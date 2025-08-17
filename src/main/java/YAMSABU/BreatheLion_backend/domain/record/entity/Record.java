package YAMSABU.BreatheLion_backend.domain.record.entity;

import YAMSABU.BreatheLion_backend.domain.chat.entity.Session;
import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.domain.record.RecordPerson;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 연관된 drawer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drawer_id")
    private Drawer drawer;

    @Column
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int severity;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    private String summary;

    @CreationTimestamp
    @Column(name ="created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name ="updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column
    private RecordCategory category;

    @OneToOne(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Session session;
  
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordPerson> recordPersons = new ArrayList<>();
}