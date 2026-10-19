package com.eqel.repository;

import com.eqel.entity.StudentProfileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfileEntity, Long> {
    Optional<StudentProfileEntity> findByUserId(Long userId);
    Optional<StudentProfileEntity> findByUserUsername(String username);
    Optional<StudentProfileEntity> findByEnrollmentNumber(String enrollmentNumber);

    @Query("SELECT s FROM StudentProfileEntity s WHERE " +
           "(:query IS NULL OR LOWER(s.user.fullName) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(s.enrollmentNumber) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR LOWER(s.department) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<StudentProfileEntity> searchStudents(@Param("query") String query, Pageable pageable);
}
