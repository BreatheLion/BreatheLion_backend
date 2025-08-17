package YAMSABU.BreatheLion_backend.domain.drawer.repository;

import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DrawerRepository extends JpaRepository<Drawer,Long> {
    boolean existsByName(String name);
    List<Drawer> findAllByOrderByCreatedAtDesc();
}
