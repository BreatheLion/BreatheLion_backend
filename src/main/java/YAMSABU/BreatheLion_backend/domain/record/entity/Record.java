package YAMSABU.BreatheLion_backend.domain.record.entity;

import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.domain.person.entity.Person;
import YAMSABU.BreatheLion_backend.domain.person.entity.PersonRole;
import YAMSABU.BreatheLion_backend.domain.record.RecordPerson;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Column
    private String content;

    @Column
    private Integer severity;

    @Column
    private String location;

    @Column
    private LocalDateTime occurredAt;

    private String summary;

    @CreationTimestamp
    @Column(name ="created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name ="updated_at")
    private LocalDateTime updatedAt;

    @Builder.Default
    @ElementCollection(targetClass = RecordCategory.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    @CollectionTable(name = "record_categories", joinColumns = @JoinColumn(name = "record_id"))
    private Set<RecordCategory> categories = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordPerson> recordPersons = new ArrayList<>();

    // 기본값이 DRAFT고 저장완료 후, FINALIZED로 변경하면 됨
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RecordStatus recordStatus = RecordStatus.DRAFT;

    public void add(Person person, PersonRole role) {
        RecordPerson rp = RecordPerson.of(this, person, role);
        this.recordPersons.add(rp);
    }

    public void clear() {
        this.recordPersons.clear();
    }
}