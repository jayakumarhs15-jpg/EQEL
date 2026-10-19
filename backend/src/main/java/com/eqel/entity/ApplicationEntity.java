package com.eqel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications", uniqueConstraints = {
    @UniqueConstraint(name = "uq_student_drive", columnNames = {"student_id", "drive_id"})
})
public class ApplicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private StudentProfileEntity student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "drive_id", nullable = false)
    private PlacementDriveEntity drive;

    @Column(nullable = false, length = 30)
    private String status = "APPLIED"; // APPLIED, SHORTLISTED, SELECTED, REJECTED, WITHDRAWN

    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public ApplicationEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StudentProfileEntity getStudent() { return student; }
    public void setStudent(StudentProfileEntity student) { this.student = student; }

    public PlacementDriveEntity getDrive() { return drive; }
    public void setDrive(PlacementDriveEntity drive) { this.drive = drive; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getAppliedAt() { return appliedAt; }
    public void setAppliedAt(LocalDateTime appliedAt) { this.appliedAt = appliedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
