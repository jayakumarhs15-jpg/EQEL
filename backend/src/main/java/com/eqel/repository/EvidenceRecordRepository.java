package com.eqel.repository;

import com.eqel.entity.EvidenceRecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EvidenceRecordRepository extends JpaRepository<EvidenceRecordEntity, Long> {
    List<EvidenceRecordEntity> findByStudentId(Long studentId);
    List<EvidenceRecordEntity> findByStudentIdAndStatus(Long studentId, String status);

    @Query("SELECT e FROM EvidenceRecordEntity e WHERE " +
           "(:status IS NULL OR e.status = :status)")
    Page<EvidenceRecordEntity> findByStatus(@Param("status") String status, Pageable pageable);
}
