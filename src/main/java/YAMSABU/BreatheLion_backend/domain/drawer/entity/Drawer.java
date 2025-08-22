package YAMSABU.BreatheLion_backend.domain.drawer.entity;

import YAMSABU.BreatheLion_backend.domain.organization.entity.DrawerOrganization;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
import java.util.List;


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
    @OneToMany(mappedBy = "drawer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DrawerOrganization> drawerOrganizations = new ArrayList<>();
}
