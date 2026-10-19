package com.eqel.repository;

import com.eqel.entity.AuditEventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEventEntity, Long> {
    @Query("SELECT a FROM AuditEventEntity a WHERE " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:entityType IS NULL OR a.entityType = :entityType)")
    Page<AuditEventEntity> searchAuditEvents(
            @Param("action") String action,
            @Param("entityType") String entityType,
            Pageable pageable);
}
