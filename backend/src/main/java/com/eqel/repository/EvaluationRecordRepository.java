package com.eqel.repository;

import com.eqel.entity.EvaluationRecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRecordRepository extends JpaRepository<EvaluationRecordEntity, Long> {
    List<EvaluationRecordEntity> findByStudentId(Long studentId);
    List<EvaluationRecordEntity> findByDriveId(Long driveId);
    Optional<EvaluationRecordEntity> findTopByStudentIdAndDriveIdOrderByEvaluatedAtDesc(Long studentId, Long driveId);

    @Query("SELECT ev FROM EvaluationRecordEntity ev WHERE " +
           "(:outcome IS NULL OR ev.overallOutcome = :outcome) AND " +
           "(:driveId IS NULL OR ev.drive.id = :driveId) AND " +
           "(:studentId IS NULL OR ev.student.id = :studentId)")
    Page<EvaluationRecordEntity> searchEvaluations(
            @Param("outcome") String outcome,
            @Param("driveId") Long driveId,
            @Param("studentId") Long studentId,
            Pageable pageable);
}
