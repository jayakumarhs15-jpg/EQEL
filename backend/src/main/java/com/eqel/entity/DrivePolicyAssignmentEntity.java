package com.eqel.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "drive_policy_assignments", uniqueConstraints = {
    @UniqueConstraint(name = "uq_drive_policy", columnNames = {"drive_id", "policy_version_id"})
})
public class DrivePolicyAssignmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drive_id", nullable = false)
    private PlacementDriveEntity drive;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "policy_version_id", nullable = false)
    private PolicyVersionEntity policyVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    private UserEntity assignedBy;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt = LocalDateTime.now();

    public DrivePolicyAssignmentEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PlacementDriveEntity getDrive() { return drive; }
    public void setDrive(PlacementDriveEntity drive) { this.drive = drive; }

    public PolicyVersionEntity getPolicyVersion() { return policyVersion; }
    public void setPolicyVersion(PolicyVersionEntity policyVersion) { this.policyVersion = policyVersion; }

    public UserEntity getAssignedBy() { return assignedBy; }
    public void setAssignedBy(UserEntity assignedBy) { this.assignedBy = assignedBy; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
}
