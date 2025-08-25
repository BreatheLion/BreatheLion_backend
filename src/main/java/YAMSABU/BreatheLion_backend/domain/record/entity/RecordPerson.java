package YAMSABU.BreatheLion_backend.domain.record.entity;

import YAMSABU.BreatheLion_backend.domain.person.entity.Person;
import YAMSABU.BreatheLion_backend.domain.person.entity.PersonRole;
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
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "record_persons")
public class RecordPerson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "record_id", nullable = false)
    private Record record;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    // ASSAILANT, WITNESS(가해자, 목격자)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PersonRole role;

    public static RecordPerson of(Record record, Person person, PersonRole role) {
        RecordPerson rp = new RecordPerson();
        rp.record = record;
        rp.person = person;
        rp.role = role;
        return rp;
    }
}
