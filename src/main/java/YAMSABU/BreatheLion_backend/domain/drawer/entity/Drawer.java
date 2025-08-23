package YAMSABU.BreatheLion_backend.domain.drawer.entity;

import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.organization.entity.Organization;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(name = "drawers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Drawer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String summary;

    private String action;

    @OneToMany(mappedBy = "drawer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Law> relatedLaws = new ArrayList<>();

    @Column(name = "record_count")
    private Long recordCount;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder.Default
    @ManyToMany  // 단방향
    @JoinTable(
            name = "drawer_organizations",
            joinColumns = @JoinColumn(name = "drawer_id"),
            inverseJoinColumns = @JoinColumn(name = "organization_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_drawer_org", columnNames = {"drawer_id", "organization_id"}
            )
    )
    private Set<Organization> organizations = new HashSet<>();

    public void addLaw(Law law) {
        this.relatedLaws.add(law);
        law.setDrawer(this);
    }

    public void addOrganization(Organization org) {
        this.organizations.add(org); // Set이라 중복 자동 제거
    }
}
