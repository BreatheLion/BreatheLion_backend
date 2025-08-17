package YAMSABU.BreatheLion_backend.domain.drawer.repository;

import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DrawerRepository extends JpaRepository<Drawer,Long> {
    Optional<Drawer> findByName(String name);
}
