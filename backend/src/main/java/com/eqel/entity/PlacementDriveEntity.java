package com.eqel.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "placement_drives")
public class PlacementDriveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id", nullable = false)
    private CompanyEntity company;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "job_role", length = 200)
    private String jobRole;

    @Column(name = "package_lpa", precision = 6, scale = 2)
    private BigDecimal packageLpa;

    @Column(name = "drive_status", nullable = false, length = 30)
    private String driveStatus = "DRAFT"; // DRAFT, OPEN, CLOSED, COMPLETED, CANCELLED

    @Column(name = "registration_open")
    private LocalDateTime registrationOpen;

    @Column(name = "registration_close")
    private LocalDateTime registrationClose;

    @Column(name = "drive_date")
    private LocalDate driveDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public PlacementDriveEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CompanyEntity getCompany() { return company; }
    public void setCompany(CompanyEntity company) { this.company = company; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getJobRole() { return jobRole; }
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }

    public BigDecimal getPackageLpa() { return packageLpa; }
    public void setPackageLpa(BigDecimal packageLpa) { this.packageLpa = packageLpa; }

    public String getDriveStatus() { return driveStatus; }
    public void setDriveStatus(String driveStatus) { this.driveStatus = driveStatus; }

    public LocalDateTime getRegistrationOpen() { return registrationOpen; }
    public void setRegistrationOpen(LocalDateTime registrationOpen) { this.registrationOpen = registrationOpen; }

    public LocalDateTime getRegistrationClose() { return registrationClose; }
    public void setRegistrationClose(LocalDateTime registrationClose) { this.registrationClose = registrationClose; }

    public LocalDate getDriveDate() { return driveDate; }
    public void setDriveDate(LocalDate driveDate) { this.driveDate = driveDate; }

    public UserEntity getCreatedBy() { return createdBy; }
    public void setCreatedBy(UserEntity createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
