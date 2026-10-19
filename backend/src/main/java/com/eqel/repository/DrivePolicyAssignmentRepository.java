package com.eqel.repository;

import com.eqel.entity.DrivePolicyAssignmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DrivePolicyAssignmentRepository extends JpaRepository<DrivePolicyAssignmentEntity, Long> {
    Optional<DrivePolicyAssignmentEntity> findByDriveId(Long driveId);
}
