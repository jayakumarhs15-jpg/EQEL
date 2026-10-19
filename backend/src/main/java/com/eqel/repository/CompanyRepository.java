package com.eqel.repository;

import com.eqel.entity.CompanyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
    Optional<CompanyEntity> findByName(String name);

    @Query("SELECT c FROM CompanyEntity c WHERE " +
           "(:query IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(c.industry) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<CompanyEntity> searchCompanies(@Param("query") String query, Pageable pageable);
}
