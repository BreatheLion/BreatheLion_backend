package YAMSABU.BreatheLion_backend.person.entity;

import YAMSABU.BreatheLion_backend.record.RecordPerson;
import YAMSABU.BreatheLion_backend.record.entity.RecordCategory;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "persons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String name;

    // ASSAILANT, WITNESS(가해자, 목격자)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PersonRole role;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordPerson> recordPersons = new ArrayList<>();
}
