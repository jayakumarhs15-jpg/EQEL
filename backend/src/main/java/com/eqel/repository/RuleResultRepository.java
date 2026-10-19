package com.eqel.repository;

import com.eqel.entity.RuleResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RuleResultRepository extends JpaRepository<RuleResultEntity, Long> {
    List<RuleResultEntity> findByEvaluationId(Long evaluationId);
}
