-- =================================================================
-- V1__initial_schema.sql — EQEL Foundation Schema
-- Users, Roles, Student Profiles, Companies, Placement Drives,
-- Evidence, Policies, Evaluations, Applications, Audit
-- =================================================================

-- ---------------------------------------------------------------
-- USERS & ROLES
-- ---------------------------------------------------------------
CREATE TABLE roles (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE users (
    id            BIGINT        AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(100)  NOT NULL UNIQUE,
    email         VARCHAR(255)  NOT NULL UNIQUE,
    password_hash VARCHAR(255)  NOT NULL,
    full_name     VARCHAR(200)  NOT NULL,
    role_id       BIGINT        NOT NULL,
    is_active     BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_users_role FOREIGN KEY (role_id) REFERENCES roles(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email    ON users(email);
CREATE INDEX idx_users_role     ON users(role_id);

-- ---------------------------------------------------------------
-- STUDENT PROFILES
-- ---------------------------------------------------------------
CREATE TABLE student_profiles (
    id                BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT       NOT NULL UNIQUE,
    enrollment_number VARCHAR(50)  NOT NULL UNIQUE,
    department        VARCHAR(100),
    program           VARCHAR(100),
    semester          INT,
    cgpa              DECIMAL(4,2),
    backlogs          INT          DEFAULT 0,
    phone             VARCHAR(20),
    date_of_birth     DATE,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- COMPANIES
-- ---------------------------------------------------------------
CREATE TABLE companies (
    id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(200)  NOT NULL,
    industry    VARCHAR(100),
    website     VARCHAR(255),
    description TEXT,
    is_active   BOOLEAN       NOT NULL DEFAULT TRUE,
    created_by  BIGINT,
    created_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_company_creator FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- PLACEMENT DRIVES
-- ---------------------------------------------------------------
CREATE TABLE placement_drives (
    id               BIGINT       AUTO_INCREMENT PRIMARY KEY,
    company_id       BIGINT       NOT NULL,
    title            VARCHAR(200) NOT NULL,
    description      TEXT,
    job_role         VARCHAR(200),
    package_lpa      DECIMAL(6,2),
    drive_status     VARCHAR(30)  NOT NULL DEFAULT 'DRAFT',
    registration_open  TIMESTAMP,
    registration_close TIMESTAMP,
    drive_date       DATE,
    created_by       BIGINT,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_drive_company FOREIGN KEY (company_id) REFERENCES companies(id),
    CONSTRAINT fk_drive_creator FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_drives_status ON placement_drives(drive_status);

-- ---------------------------------------------------------------
-- ELIGIBILITY POLICIES (versioned)
-- ---------------------------------------------------------------
CREATE TABLE eligibility_policies (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(200) NOT NULL,
    description TEXT,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_by  BIGINT,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_policy_creator FOREIGN KEY (created_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE policy_versions (
    id              BIGINT   AUTO_INCREMENT PRIMARY KEY,
    policy_id       BIGINT   NOT NULL,
    version_number  INT      NOT NULL,
    notes           TEXT,
    is_published     BOOLEAN  NOT NULL DEFAULT FALSE,
    created_by      BIGINT,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pv_policy  FOREIGN KEY (policy_id)  REFERENCES eligibility_policies(id),
    CONSTRAINT fk_pv_creator FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT uq_policy_version UNIQUE (policy_id, version_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE policy_rules (
    id                 BIGINT       AUTO_INCREMENT PRIMARY KEY,
    policy_version_id  BIGINT       NOT NULL,
    rule_code          VARCHAR(50)  NOT NULL,
    rule_description   VARCHAR(500) NOT NULL,
    field_name         VARCHAR(100) NOT NULL,
    operator           VARCHAR(30)  NOT NULL,
    threshold_value    VARCHAR(100) NOT NULL,
    is_mandatory       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rule_version FOREIGN KEY (policy_version_id) REFERENCES policy_versions(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Link drives to policy versions
CREATE TABLE drive_policy_assignments (
    id                BIGINT    AUTO_INCREMENT PRIMARY KEY,
    drive_id          BIGINT    NOT NULL,
    policy_version_id BIGINT    NOT NULL,
    assigned_by       BIGINT,
    assigned_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dpa_drive   FOREIGN KEY (drive_id)          REFERENCES placement_drives(id),
    CONSTRAINT fk_dpa_version FOREIGN KEY (policy_version_id) REFERENCES policy_versions(id),
    CONSTRAINT fk_dpa_user    FOREIGN KEY (assigned_by)       REFERENCES users(id),
    CONSTRAINT uq_drive_policy UNIQUE (drive_id, policy_version_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- EVIDENCE
-- ---------------------------------------------------------------
CREATE TABLE evidence_records (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT       NOT NULL,
    evidence_type   VARCHAR(50)  NOT NULL,
    field_name      VARCHAR(100) NOT NULL,
    field_value     VARCHAR(500),
    status          VARCHAR(30)  NOT NULL DEFAULT 'PENDING',
    revision        INT          NOT NULL DEFAULT 1,
    source          VARCHAR(200),
    submitted_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    verified_at     TIMESTAMP    NULL,
    verified_by     BIGINT       NULL,
    reviewer_notes  TEXT,
    CONSTRAINT fk_evidence_student  FOREIGN KEY (student_id)  REFERENCES student_profiles(id),
    CONSTRAINT fk_evidence_verifier FOREIGN KEY (verified_by) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_evidence_student ON evidence_records(student_id);
CREATE INDEX idx_evidence_status  ON evidence_records(status);

-- ---------------------------------------------------------------
-- ELIGIBILITY EVALUATIONS
-- ---------------------------------------------------------------
CREATE TABLE evaluation_records (
    id                     BIGINT       AUTO_INCREMENT PRIMARY KEY,
    student_id             BIGINT       NOT NULL,
    drive_id               BIGINT       NOT NULL,
    policy_version_id      BIGINT       NOT NULL,
    overall_outcome        VARCHAR(30)  NOT NULL,
    explanation            TEXT,
    decision_engine_version VARCHAR(20) NOT NULL,
    evidence_snapshot      JSON,
    evaluated_by           BIGINT,
    evaluated_at           TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_eval_student FOREIGN KEY (student_id)        REFERENCES student_profiles(id),
    CONSTRAINT fk_eval_drive   FOREIGN KEY (drive_id)          REFERENCES placement_drives(id),
    CONSTRAINT fk_eval_version FOREIGN KEY (policy_version_id) REFERENCES policy_versions(id),
    CONSTRAINT fk_eval_user    FOREIGN KEY (evaluated_by)      REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_eval_student ON evaluation_records(student_id);
CREATE INDEX idx_eval_drive   ON evaluation_records(drive_id);

CREATE TABLE rule_results (
    id                  BIGINT       AUTO_INCREMENT PRIMARY KEY,
    evaluation_id       BIGINT       NOT NULL,
    rule_id             BIGINT       NOT NULL,
    rule_code           VARCHAR(50)  NOT NULL,
    result              VARCHAR(30)  NOT NULL,
    evidence_value      VARCHAR(500),
    threshold_value     VARCHAR(100),
    explanation         VARCHAR(500),
    CONSTRAINT fk_rr_eval FOREIGN KEY (evaluation_id) REFERENCES evaluation_records(id),
    CONSTRAINT fk_rr_rule FOREIGN KEY (rule_id)       REFERENCES policy_rules(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- APPLICATIONS
-- ---------------------------------------------------------------
CREATE TABLE applications (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    student_id      BIGINT       NOT NULL,
    drive_id        BIGINT       NOT NULL,
    status          VARCHAR(30)  NOT NULL DEFAULT 'APPLIED',
    applied_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_app_student FOREIGN KEY (student_id) REFERENCES student_profiles(id),
    CONSTRAINT fk_app_drive   FOREIGN KEY (drive_id)   REFERENCES placement_drives(id),
    CONSTRAINT uq_student_drive UNIQUE (student_id, drive_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------
-- AUDIT LOG
-- ---------------------------------------------------------------
CREATE TABLE audit_events (
    id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT,
    action      VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id   BIGINT,
    details     TEXT,
    ip_address  VARCHAR(45),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_audit_user   ON audit_events(user_id);
CREATE INDEX idx_audit_action ON audit_events(action);
CREATE INDEX idx_audit_time   ON audit_events(created_at);
