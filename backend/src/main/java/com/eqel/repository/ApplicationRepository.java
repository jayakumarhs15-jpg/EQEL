package com.eqel.repository;

import com.eqel.entity.ApplicationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {
    List<ApplicationEntity> findByStudentId(Long studentId);
    List<ApplicationEntity> findByDriveId(Long driveId);
    Optional<ApplicationEntity> findByStudentIdAndDriveId(Long studentId, Long driveId);
    boolean existsByStudentIdAndDriveId(Long studentId, Long driveId);
    Page<ApplicationEntity> findByDriveId(Long driveId, Pageable pageable);
}
