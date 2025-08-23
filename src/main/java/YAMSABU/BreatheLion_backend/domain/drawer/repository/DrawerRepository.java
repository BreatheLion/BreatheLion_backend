package YAMSABU.BreatheLion_backend.domain.drawer.repository;

import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DrawerRepository extends JpaRepository<Drawer, Long> {
    boolean existsByName(String name);

    List<Drawer> findAllByOrderByCreatedAtDesc();

    Optional<Drawer> findByName(String name);

    @Modifying
    @Query("UPDATE Drawer d SET d.recordCount = COALESCE(d.recordCount, 0) + 1 WHERE d.id = :drawerId")
    void incrementRecordCount(@Param("drawerId") Long drawerId);

    @Modifying
    @Query("UPDATE Drawer d SET d.recordCount = COALESCE(d.recordCount, 0) - 1 WHERE d.id = :drawerId")
    void decrementRecordCount(@Param("drawerId") Long drawerId);
}
