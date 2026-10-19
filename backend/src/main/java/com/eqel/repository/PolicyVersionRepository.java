package com.eqel.repository;

import com.eqel.entity.PolicyVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyVersionRepository extends JpaRepository<PolicyVersionEntity, Long> {
    List<PolicyVersionEntity> findByPolicyId(Long policyId);
    Optional<PolicyVersionEntity> findByPolicyIdAndVersionNumber(Long policyId, Integer versionNumber);
}
