package com.eqel.repository;

import com.eqel.entity.EligibilityPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EligibilityPolicyRepository extends JpaRepository<EligibilityPolicyEntity, Long> {
    Optional<EligibilityPolicyEntity> findByName(String name);
}
