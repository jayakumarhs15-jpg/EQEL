# EQEL — Database & Domain Model Documentation

## 1. Relational Design
EQEL's persistence model enforces strict referential integrity, unique constraints, and temporal tracking via MySQL 8.0 and Flyway.

```
       +---------------+       +------------------+
       |     roles     |       |   audit_events   |
       +---------------+       +------------------+
               | 1                      ^
               |                        |
               v N                      |
       +---------------+                |
       |     users     |----------------+
       +---------------+
          |          |
      1:1 |          | 1:N
          v          +-----------------------+
   +------------------+                      |
   | student_profiles |                      |
   +------------------+                      |
      |             |                        |
  1:N |         1:N |                        |
      v             v                        v
+------------+  +--------------------+  +--------------------+
|  evidence  |  | evaluation_records |  |     companies      |
|  records   |  +--------------------+  +--------------------+
+------------+            |                      | 1:N
                          | 1:N                  v
                          v             +--------------------+
                  +---------------+     |  placement_drives  |
                  |  rule_results |     +--------------------+
                  +---------------+              |
                                                 | 1:N
                                                 v
                                        +--------------------+
                                        |  drive_policy_map  |
                                        +--------------------+
                                                 |
                                                 v N:1
+----------------------+ 1:N  +-----------------+ 1:N  +--------------+
| eligibility_policies |----->| policy_versions |----->| policy_rules |
+----------------------+      +-----------------+      +--------------+
```

## 2. Table Specifications
- **`roles`**: System roles (`ADMIN`, `PLACEMENT_OFFICER`, `STUDENT`).
- **`users`**: User identities, hashed passwords (`BCrypt`), active flags, role foreign key.
- **`student_profiles`**: 1-to-1 extension of `users` for student role: enrollment number, program, CGPA, backlogs, department, semester.
- **`companies`**: Target employers and placement partners.
- **`placement_drives`**: Job roles, CTC (package LPA), registration windows, drive dates, drive lifecycle status (`DRAFT`, `OPEN`, `CLOSED`, `COMPLETED`, `CANCELLED`).
- **`eligibility_policies`**: Named policies (e.g., "Tier 1 Software Engineering Policy").
- **`policy_versions`**: Immutable versions (`v1`, `v2`) with notes and publication state.
- **`policy_rules`**: Rules per version (`MIN_CGPA >= 7.5`, `MAX_BACKLOGS <= 0`, `DEPT IN [CSE, IT]`).
- **`evidence_records`**: Versioned student claims (`CGPA`, `BACKLOGS`, `DEGREE_CERTIFICATE`), verification status (`PENDING`, `VERIFIED`, `REJECTED`, `CONFLICTING`), reviewer audit.
- **`evaluation_records`**: Recorded eligibility verdicts: `ELIGIBLE`, `INELIGIBLE`, `UNVERIFIABLE`, snapshot of input evidence, policy version link, decision-engine version (`1.0.0`).
- **`rule_results`**: Granular per-rule evaluation results (`SATISFIED`, `VIOLATED`, `UNVERIFIABLE`), evidence value, threshold, and operational explanation.
- **`applications`**: Drive application records with lifecycle status (`APPLIED`, `SHORTLISTED`, `SELECTED`, `REJECTED`, `WITHDRAWN`).
- **`audit_events`**: Security and administrative audit trail with IP address and timestamp.

## 3. Migration Strategy
All migrations are sequential, immutable Flyway scripts:
- `V1__initial_schema.sql`: Full schema with constraints and indexes.
- `V2__seed_roles.sql`: Standard role population.
- Subsequent migrations must be forward-only.
