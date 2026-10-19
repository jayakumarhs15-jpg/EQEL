package com.eqel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "evaluation_records")
public class EvaluationRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfileEntity student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "drive_id", nullable = false)
    private PlacementDriveEntity drive;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "policy_version_id", nullable = false)
    private PolicyVersionEntity policyVersion;

    @Column(name = "overall_outcome", nullable = false, length = 30)
    private String overallOutcome; // ELIGIBLE, INELIGIBLE, UNVERIFIABLE

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "decision_engine_version", nullable = false, length = 20)
    private String decisionEngineVersion = "1.0.0";

    @Column(name = "evidence_snapshot", columnDefinition = "JSON")
    private String evidenceSnapshot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluated_by")
    private UserEntity evaluatedBy;

    @OneToMany(mappedBy = "evaluation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RuleResultEntity> ruleResults = new ArrayList<>();

    @Column(name = "evaluated_at", nullable = false, updatable = false)
    private LocalDateTime evaluatedAt = LocalDateTime.now();

    public EvaluationRecordEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentProfileEntity getStudent() { return student; }
    public void setStudent(StudentProfileEntity student) { this.student = student; }

    public PlacementDriveEntity getDrive() { return drive; }
    public void setDrive(PlacementDriveEntity drive) { this.drive = drive; }

    public PolicyVersionEntity getPolicyVersion() { return policyVersion; }
    public void setPolicyVersion(PolicyVersionEntity policyVersion) { this.policyVersion = policyVersion; }

    public String getOverallOutcome() { return overallOutcome; }
    public void setOverallOutcome(String overallOutcome) { this.overallOutcome = overallOutcome; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getDecisionEngineVersion() { return decisionEngineVersion; }
    public void setDecisionEngineVersion(String decisionEngineVersion) { this.decisionEngineVersion = decisionEngineVersion; }

    public String getEvidenceSnapshot() { return evidenceSnapshot; }
    public void setEvidenceSnapshot(String evidenceSnapshot) { this.evidenceSnapshot = evidenceSnapshot; }

    public UserEntity getEvaluatedBy() { return evaluatedBy; }
    public void setEvaluatedBy(UserEntity evaluatedBy) { this.evaluatedBy = evaluatedBy; }

    public List<RuleResultEntity> getRuleResults() { return ruleResults; }
    public void setRuleResults(List<RuleResultEntity> ruleResults) { this.ruleResults = ruleResults; }

    public LocalDateTime getEvaluatedAt() { return evaluatedAt; }
    public void setEvaluatedAt(LocalDateTime evaluatedAt) { this.evaluatedAt = evaluatedAt; }
}
