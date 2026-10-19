package com.eqel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evidence_records")
public class EvidenceRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfileEntity student;

    @Column(name = "evidence_type", nullable = false, length = 50)
    private String evidenceType; // CGPA, BACKLOGS, DEGREE, RESUME, CERTIFICATE

    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;

    @Column(name = "field_value", length = 500)
    private String fieldValue;

    @Column(nullable = false, length = 30)
    private String status = "PENDING"; // PENDING, VERIFIED, REJECTED, CONFLICTING

    @Column(nullable = false)
    private Integer revision = 1;

    @Column(length = 200)
    private String source;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private UserEntity verifiedBy;

    @Column(name = "reviewer_notes", columnDefinition = "TEXT")
    private String reviewerNotes;

    public EvidenceRecordEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentProfileEntity getStudent() { return student; }
    public void setStudent(StudentProfileEntity student) { this.student = student; }

    public String getEvidenceType() { return evidenceType; }
    public void setEvidenceType(String evidenceType) { this.evidenceType = evidenceType; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getFieldValue() { return fieldValue; }
    public void setFieldValue(String fieldValue) { this.fieldValue = fieldValue; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getRevision() { return revision; }
    public void setRevision(Integer revision) { this.revision = revision; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    public UserEntity getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(UserEntity verifiedBy) { this.verifiedBy = verifiedBy; }

    public String getReviewerNotes() { return reviewerNotes; }
    public void setReviewerNotes(String reviewerNotes) { this.reviewerNotes = reviewerNotes; }
}
