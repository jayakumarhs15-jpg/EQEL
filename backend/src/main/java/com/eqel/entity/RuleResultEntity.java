package com.eqel.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rule_results")
public class RuleResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evaluation_id", nullable = false)
    private EvaluationRecordEntity evaluation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rule_id", nullable = false)
    private PolicyRuleEntity rule;

    @Column(name = "rule_code", nullable = false, length = 50)
    private String ruleCode;

    @Column(nullable = false, length = 30)
    private String result; // SATISFIED, VIOLATED, UNVERIFIABLE

    @Column(name = "evidence_value", length = 500)
    private String evidenceValue;

    @Column(name = "threshold_value", length = 100)
    private String thresholdValue;

    @Column(length = 500)
    private String explanation;

    public RuleResultEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public EvaluationRecordEntity getEvaluation() { return evaluation; }
    public void setEvaluation(EvaluationRecordEntity evaluation) { this.evaluation = evaluation; }

    public PolicyRuleEntity getRule() { return rule; }
    public void setRule(PolicyRuleEntity rule) { this.rule = rule; }

    public String getRuleCode() { return ruleCode; }
    public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }

    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public String getEvidenceValue() { return evidenceValue; }
    public void setEvidenceValue(String evidenceValue) { this.evidenceValue = evidenceValue; }

    public String getThresholdValue() { return thresholdValue; }
    public void setThresholdValue(String thresholdValue) { this.thresholdValue = thresholdValue; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
