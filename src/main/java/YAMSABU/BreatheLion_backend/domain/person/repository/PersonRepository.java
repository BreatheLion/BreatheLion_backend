package YAMSABU.BreatheLion_backend.domain.person.repository;

import YAMSABU.BreatheLion_backend.domain.person.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByName(String name);
}
