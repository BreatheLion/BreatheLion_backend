package YAMSABU.BreatheLion_backend.domain.record.repository;

import YAMSABU.BreatheLion_backend.domain.drawer.entity.Drawer;
import YAMSABU.BreatheLion_backend.domain.record.entity.Record;
import YAMSABU.BreatheLion_backend.domain.record.entity.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByDrawer(Drawer drawer);

    // 페이징 없이 전체 FINALIZED 기록을 최신순으로 반환
    List<Record> findByRecordStatusOrderByCreatedAtDesc(RecordStatus status);

    @Query("""
        select r
        from Record r
        where r.drawer.id = :drawerId
        order by r.createdAt desc, r.id desc
    """)
    List<Record> findAllByDrawer(@Param("drawerId") Long drawerId);

    @Query("""
        select r
        from Record r
        where r.drawer.id = :drawerId
          and (
               lower(r.title)   like lower(concat('%', :kw, '%')) escape '!'
            or lower(r.content) like lower(concat('%', :kw, '%')) escape '!'
            or lower(r.summary) like lower(concat('%', :kw, '%')) escape '!'
          )
        order by r.createdAt desc, r.id desc
    """)
    List<Record> searchByDrawerAndKeyword(@Param("drawerId") Long drawerId,
                                          @Param("kw") String escapedKeyword);
    @Query("""
        select distinct r
        from Record r
        left join fetch r.recordPersons rp
        left join fetch rp.person p
        where r.drawer.id = :drawerId and r.recordStatus = :status
        """)
    List<Record> findAllForPdf(@Param("drawerId") Long drawerId,
                               @Param("status") RecordStatus status);

    List<Record> findByDrawerId(Long drawerId);
}
