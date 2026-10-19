package com.eqel.repository;

import com.eqel.entity.PlacementDriveEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PlacementDriveRepository extends JpaRepository<PlacementDriveEntity, Long> {
    List<PlacementDriveEntity> findByDriveStatus(String driveStatus);
    Optional<PlacementDriveEntity> findByTitle(String title);

    @Query("SELECT d FROM PlacementDriveEntity d WHERE " +
           "(:status IS NULL OR d.driveStatus = :status) AND " +
           "(:query IS NULL OR LOWER(d.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(d.company.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(d.jobRole) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<PlacementDriveEntity> searchDrives(@Param("query") String query, @Param("status") String status, Pageable pageable);
}
