package com.eqel.repository;

import com.eqel.entity.PolicyRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PolicyRuleRepository extends JpaRepository<PolicyRuleEntity, Long> {
    List<PolicyRuleEntity> findByPolicyVersionId(Long policyVersionId);
}
