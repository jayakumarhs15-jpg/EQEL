package com.eqel.controller;

import com.eqel.dto.ApiResponseDto;
import com.eqel.engine.EligibilityEngine;
import com.eqel.entity.*;
import com.eqel.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/placement")
@PreAuthorize("hasRole('PLACEMENT_OFFICER')")
public class PlacementController {

    private final StudentProfileRepository studentProfileRepository;
    private final CompanyRepository companyRepository;
    private final PlacementDriveRepository placementDriveRepository;
    private final EligibilityPolicyRepository eligibilityPolicyRepository;
    private final PolicyVersionRepository policyVersionRepository;
    private final DrivePolicyAssignmentRepository drivePolicyAssignmentRepository;
    private final EvidenceRecordRepository evidenceRecordRepository;
    private final EvaluationRecordRepository evaluationRecordRepository;
    private final RuleResultRepository ruleResultRepository;
    private final UserRepository userRepository;
    private final EligibilityEngine eligibilityEngine;

    public PlacementController(StudentProfileRepository studentProfileRepository,
                               CompanyRepository companyRepository,
                               PlacementDriveRepository placementDriveRepository,
                               EligibilityPolicyRepository eligibilityPolicyRepository,
                               PolicyVersionRepository policyVersionRepository,
                               DrivePolicyAssignmentRepository drivePolicyAssignmentRepository,
                               EvidenceRecordRepository evidenceRecordRepository,
                               EvaluationRecordRepository evaluationRecordRepository,
                               RuleResultRepository ruleResultRepository,
                               UserRepository userRepository,
                               EligibilityEngine eligibilityEngine) {
        this.studentProfileRepository = studentProfileRepository;
        this.companyRepository = companyRepository;
        this.placementDriveRepository = placementDriveRepository;
        this.eligibilityPolicyRepository = eligibilityPolicyRepository;
        this.policyVersionRepository = policyVersionRepository;
        this.drivePolicyAssignmentRepository = drivePolicyAssignmentRepository;
        this.evidenceRecordRepository = evidenceRecordRepository;
        this.evaluationRecordRepository = evaluationRecordRepository;
        this.ruleResultRepository = ruleResultRepository;
        this.userRepository = userRepository;
        this.eligibilityEngine = eligibilityEngine;
    }

    // ── 1. Student Directory ──
    @GetMapping("/students")
    public ResponseEntity<Page<StudentProfileEntity>> listStudents(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(studentProfileRepository.searchStudents(query, pageable));
    }

    // ── 2. Companies ──
    @GetMapping("/companies")
    public ResponseEntity<Page<CompanyEntity>> listCompanies(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(companyRepository.searchCompanies(query, PageRequest.of(page, size)));
    }

    @PostMapping("/companies")
    @Transactional
    public ResponseEntity<CompanyEntity> createCompany(@RequestBody Map<String, String> body, Authentication auth) {
        CompanyEntity company = new CompanyEntity();
        company.setName(body.get("name"));
        company.setIndustry(body.get("industry"));
        company.setWebsite(body.get("website"));
        company.setDescription(body.get("description"));

        if (auth != null) {
            userRepository.findByUsername(auth.getName()).ifPresent(company::setCreatedBy);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(companyRepository.save(company));
    }

    // ── 3. Placement Drives ──
    @GetMapping("/drives")
    public ResponseEntity<Page<PlacementDriveEntity>> listDrives(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(placementDriveRepository.searchDrives(query, status, pageable));
    }

    public static class CreateDriveRequest {
        @NotNull public Long companyId;
        @NotBlank public String title;
        public String description;
        public String jobRole;
        public BigDecimal packageLpa;
        public String driveStatus;
        public Long policyVersionId;
    }

    @PostMapping("/drives")
    @Transactional
    public ResponseEntity<PlacementDriveEntity> createDrive(@Valid @RequestBody CreateDriveRequest req, Authentication auth) {
        CompanyEntity company = companyRepository.findById(req.companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        PlacementDriveEntity drive = new PlacementDriveEntity();
        drive.setCompany(company);
        drive.setTitle(req.title);
        drive.setDescription(req.description);
        drive.setJobRole(req.jobRole);
        drive.setPackageLpa(req.packageLpa);
        drive.setDriveStatus(req.driveStatus != null ? req.driveStatus : "OPEN");
        drive.setRegistrationOpen(LocalDateTime.now());
        drive.setRegistrationClose(LocalDateTime.now().plusDays(14));
        drive.setDriveDate(LocalDate.now().plusDays(21));

        UserEntity creator = auth != null ? userRepository.findByUsername(auth.getName()).orElse(null) : null;
        drive.setCreatedBy(creator);

        PlacementDriveEntity saved = placementDriveRepository.save(drive);

        // Associate policy version if provided
        if (req.policyVersionId != null) {
            policyVersionRepository.findById(req.policyVersionId).ifPresent(pv -> {
                DrivePolicyAssignmentEntity dpa = new DrivePolicyAssignmentEntity();
                dpa.setDrive(saved);
                dpa.setPolicyVersion(pv);
                dpa.setAssignedBy(creator);
                drivePolicyAssignmentRepository.save(dpa);
            });
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ── 4. Eligibility Policies ──
    @GetMapping("/policies")
    public ResponseEntity<List<EligibilityPolicyEntity>> listPolicies() {
        return ResponseEntity.ok(eligibilityPolicyRepository.findAll());
    }

    @GetMapping("/policies/{policyId}/versions")
    public ResponseEntity<List<PolicyVersionEntity>> listPolicyVersions(@PathVariable Long policyId) {
        return ResponseEntity.ok(policyVersionRepository.findByPolicyId(policyId));
    }

    public static class CreatePolicyRuleDto {
        public String ruleCode;
        public String ruleDescription;
        public String fieldName;
        public String operator;
        public String thresholdValue;
        public boolean isMandatory = true;
    }

    public static class CreatePolicyRequest {
        public String name;
        public String description;
        public List<CreatePolicyRuleDto> rules;
    }

    @PostMapping("/policies")
    @Transactional
    public ResponseEntity<?> createPolicy(@RequestBody CreatePolicyRequest req, Authentication auth) {
        UserEntity creator = auth != null ? userRepository.findByUsername(auth.getName()).orElse(null) : null;

        EligibilityPolicyEntity policy = new EligibilityPolicyEntity();
        policy.setName(req.name);
        policy.setDescription(req.description);
        policy.setCreatedBy(creator);
        EligibilityPolicyEntity savedPolicy = eligibilityPolicyRepository.save(policy);

        // Version 1
        PolicyVersionEntity pv = new PolicyVersionEntity();
        pv.setPolicy(savedPolicy);
        pv.setVersionNumber(1);
        pv.setNotes("Initial policy definition");
        pv.setPublished(true);
        pv.setCreatedBy(creator);

        if (req.rules != null) {
            for (CreatePolicyRuleDto r : req.rules) {
                PolicyRuleEntity rule = new PolicyRuleEntity();
                rule.setPolicyVersion(pv);
                rule.setRuleCode(r.ruleCode);
                rule.setRuleDescription(r.ruleDescription);
                rule.setFieldName(r.fieldName);
                rule.setOperator(r.operator);
                rule.setThresholdValue(r.thresholdValue);
                rule.setMandatory(r.isMandatory);
                pv.getRules().add(rule);
            }
        }

        policyVersionRepository.save(pv);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPolicy);
    }

    // ── 5. Run Eligibility Evaluation ──
    public static class EvaluateRequest {
        @NotNull public Long studentId;
        @NotNull public Long driveId;
    }

    @PostMapping("/evaluations/evaluate")
    @Transactional
    public ResponseEntity<?> evaluateStudent(@Valid @RequestBody EvaluateRequest req, Authentication auth) {
        StudentProfileEntity student = studentProfileRepository.findById(req.studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + req.studentId));

        PlacementDriveEntity drive = placementDriveRepository.findById(req.driveId)
                .orElseThrow(() -> new IllegalArgumentException("Drive not found: " + req.driveId));

        // Find policy version linked to drive
        DrivePolicyAssignmentEntity assignment = drivePolicyAssignmentRepository.findByDriveId(drive.getId())
                .orElseThrow(() -> new IllegalStateException("No eligibility policy assigned to drive: " + drive.getTitle()));

        PolicyVersionEntity policyVersion = assignment.getPolicyVersion();

        // Fetch student's submitted evidence
        List<EvidenceRecordEntity> evidenceRecords = evidenceRecordRepository.findByStudentId(student.getId());

        // Run deterministic engine
        EligibilityEngine.EvaluationVerdict verdict = eligibilityEngine.evaluate(student, policyVersion, evidenceRecords);

        // Record Decision into Ledger
        EvaluationRecordEntity record = new EvaluationRecordEntity();
        record.setStudent(student);
        record.setDrive(drive);
        record.setPolicyVersion(policyVersion);
        record.setOverallOutcome(verdict.getOverallOutcome().name());
        record.setExplanation(verdict.getExplanation());
        record.setDecisionEngineVersion(verdict.getEngineVersion());

        if (auth != null) {
            userRepository.findByUsername(auth.getName()).ifPresent(record::setEvaluatedBy);
        }

        EvaluationRecordEntity savedRecord = evaluationRecordRepository.save(record);

        // Record granular rule-level results
        List<RuleResultEntity> ruleEntities = new ArrayList<>();
        for (EligibilityEngine.RuleEvaluationResult rr : verdict.getRuleResults()) {
            RuleResultEntity rre = new RuleResultEntity();
            rre.setEvaluation(savedRecord);
            rre.setRule(rr.getRule());
            rre.setRuleCode(rr.getRule().getRuleCode());
            rre.setResult(rr.getOutcome().name());
            rre.setEvidenceValue(rr.getEvidenceValue());
            rre.setThresholdValue(rr.getThresholdValue());
            rre.setExplanation(rr.getExplanation());
            ruleEntities.add(rre);
        }
        ruleResultRepository.saveAll(ruleEntities);
        savedRecord.setRuleResults(ruleEntities);

        return ResponseEntity.ok(savedRecord);
    }

    // ── 6. Evidence Review ──
    @GetMapping("/evidence/pending")
    public ResponseEntity<Page<EvidenceRecordEntity>> listPendingEvidence(
            @RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(evidenceRecordRepository.findByStatus(status, PageRequest.of(page, size)));
    }

    @PutMapping("/evidence/{id}/verify")
    @Transactional
    public ResponseEntity<?> verifyEvidence(@PathVariable Long id,
                                            @RequestBody Map<String, String> body,
                                            Authentication auth) {
        EvidenceRecordEntity evidence = evidenceRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Evidence record not found"));

        String newStatus = body.getOrDefault("status", "VERIFIED").toUpperCase();
        evidence.setStatus(newStatus);
        evidence.setReviewerNotes(body.get("notes"));
        evidence.setVerifiedAt(LocalDateTime.now());

        if (auth != null) {
            userRepository.findByUsername(auth.getName()).ifPresent(evidence::setVerifiedBy);
        }

        return ResponseEntity.ok(evidenceRecordRepository.save(evidence));
    }

    // ── 7. Decision Ledger & History ──
    @GetMapping("/evaluations/history")
    public ResponseEntity<Page<EvaluationRecordEntity>> listHistory(
            @RequestParam(required = false) String outcome,
            @RequestParam(required = false) Long driveId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("evaluatedAt").descending());
        return ResponseEntity.ok(evaluationRecordRepository.searchEvaluations(outcome, driveId, studentId, pageable));
    }
}
