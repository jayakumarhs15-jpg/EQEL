package com.eqel.controller;

import com.eqel.dto.ApiResponseDto;
import com.eqel.entity.*;
import com.eqel.repository.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/student")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentProfileRepository studentProfileRepository;
    private final EvidenceRecordRepository evidenceRecordRepository;
    private final PlacementDriveRepository placementDriveRepository;
    private final EvaluationRecordRepository evaluationRecordRepository;
    private final ApplicationRepository applicationRepository;

    public StudentController(StudentProfileRepository studentProfileRepository,
                             EvidenceRecordRepository evidenceRecordRepository,
                             PlacementDriveRepository placementDriveRepository,
                             EvaluationRecordRepository evaluationRecordRepository,
                             ApplicationRepository applicationRepository) {
        this.studentProfileRepository = studentProfileRepository;
        this.evidenceRecordRepository = evidenceRecordRepository;
        this.placementDriveRepository = placementDriveRepository;
        this.evaluationRecordRepository = evaluationRecordRepository;
        this.applicationRepository = applicationRepository;
    }

    private StudentProfileEntity getCurrentStudent(Authentication auth) {
        if (auth == null) {
            throw new IllegalStateException("Unauthenticated request");
        }
        return studentProfileRepository.findByUserUsername(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Student profile not found for user: " + auth.getName()));
    }

    // ── 1. My Profile ──
    @GetMapping("/profile")
    public ResponseEntity<StudentProfileEntity> getProfile(Authentication auth) {
        return ResponseEntity.ok(getCurrentStudent(auth));
    }

    @PutMapping("/profile")
    @Transactional
    public ResponseEntity<StudentProfileEntity> updateProfile(@RequestBody Map<String, String> body, Authentication auth) {
        StudentProfileEntity student = getCurrentStudent(auth);

        // Only permitted non-academic contact edits
        if (body.containsKey("phone")) {
            student.setPhone(body.get("phone"));
        }
        return ResponseEntity.ok(studentProfileRepository.save(student));
    }

    // ── 2. My Evidence ──
    @GetMapping("/evidence")
    public ResponseEntity<List<EvidenceRecordEntity>> getMyEvidence(Authentication auth) {
        StudentProfileEntity student = getCurrentStudent(auth);
        return ResponseEntity.ok(evidenceRecordRepository.findByStudentId(student.getId()));
    }

    public static class SubmitEvidenceRequest {
        @NotBlank public String evidenceType;
        @NotBlank public String fieldName;
        @NotBlank public String fieldValue;
        public String source;
    }

    @PostMapping("/evidence")
    @Transactional
    public ResponseEntity<EvidenceRecordEntity> submitEvidence(@Valid @RequestBody SubmitEvidenceRequest req, Authentication auth) {
        StudentProfileEntity student = getCurrentStudent(auth);

        EvidenceRecordEntity record = new EvidenceRecordEntity();
        record.setStudent(student);
        record.setEvidenceType(req.evidenceType);
        record.setFieldName(req.fieldName);
        record.setFieldValue(req.fieldValue);
        record.setSource(req.source != null ? req.source : "Student Manual Submission");
        record.setStatus("PENDING"); // New evidence always enters PENDING review
        record.setRevision(1);

        return ResponseEntity.status(HttpStatus.CREATED).body(evidenceRecordRepository.save(record));
    }

    // ── 3. Open Placement Drives ──
    @GetMapping("/drives")
    public ResponseEntity<List<PlacementDriveEntity>> getOpenDrives() {
        return ResponseEntity.ok(placementDriveRepository.findByDriveStatus("OPEN"));
    }

    // ── 4. My Eligibility Verdicts ──
    @GetMapping("/eligibility")
    public ResponseEntity<List<EvaluationRecordEntity>> getMyEligibility(Authentication auth) {
        StudentProfileEntity student = getCurrentStudent(auth);
        return ResponseEntity.ok(evaluationRecordRepository.findByStudentId(student.getId()));
    }

    // ── 5. My Applications ──
    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationEntity>> getMyApplications(Authentication auth) {
        StudentProfileEntity student = getCurrentStudent(auth);
        return ResponseEntity.ok(applicationRepository.findByStudentId(student.getId()));
    }

    @PostMapping("/applications/{driveId}")
    @Transactional
    public ResponseEntity<?> applyForDrive(@PathVariable Long driveId, Authentication auth) {
        StudentProfileEntity student = getCurrentStudent(auth);

        PlacementDriveEntity drive = placementDriveRepository.findById(driveId)
                .orElseThrow(() -> new IllegalArgumentException("Drive not found: " + driveId));

        if (!"OPEN".equalsIgnoreCase(drive.getDriveStatus())) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("Drive registration is currently closed."));
        }

        if (applicationRepository.existsByStudentIdAndDriveId(student.getId(), drive.getId())) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error("You have already applied for this drive."));
        }

        // Verify eligibility requirement before application submission
        Optional<EvaluationRecordEntity> latestEval = evaluationRecordRepository
                .findTopByStudentIdAndDriveIdOrderByEvaluatedAtDesc(student.getId(), drive.getId());

        if (latestEval.isEmpty() || !"ELIGIBLE".equalsIgnoreCase(latestEval.get().getOverallOutcome())) {
            return ResponseEntity.badRequest().body(ApiResponseDto.error(
                    "You cannot apply without a verified ELIGIBLE verdict. Current status: " +
                    (latestEval.map(EvaluationRecordEntity::getOverallOutcome).orElse("NOT EVALUATED"))
            ));
        }

        ApplicationEntity application = new ApplicationEntity();
        application.setStudent(student);
        application.setDrive(drive);
        application.setStatus("APPLIED");

        return ResponseEntity.status(HttpStatus.CREATED).body(applicationRepository.save(application));
    }
}
