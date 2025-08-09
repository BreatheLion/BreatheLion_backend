package YAMSABU.BreatheLion_backend.record.entity;

import YAMSABU.BreatheLion_backend.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.record.RecordPerson;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    @JoinColumn(name = "drawer_id", nullable = false)
    private Drawer drawer;

    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordPerson> recordPersons = new ArrayList<>();

    private String title;

    private String content;

    private int severity;

    private String location;

    private LocalDateTime occurredAt;

    private String summary;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // 엔티티가 처음 저장되기 전(DB에 INSERT 되기 직전)에 호출
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // 엔티티가 DB에 UPDATE 되기 직전에 호출
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordCategory category;

}