-- =================================================================
-- V3__demo_records.sql — 50 Safe, Repeatable Demo Records for EQEL
-- 
-- Table Row Distribution (Exactly 50 Rows Total):
--   1.  users                     :  5 rows (demo.student01 - demo.student05)
--   2.  student_profiles          :  5 rows (EQEL2026CS001 - EQEL2026ME005)
--   3.  companies                 :  2 rows (NexGen Cloud, Apex Cybernetics)
--   4.  placement_drives          :  2 rows (NexGen SWE, Apex Systems Eng)
--   5.  eligibility_policies      :  2 rows (NexGen Policy, Apex Policy)
--   6.  policy_versions           :  2 rows (v1 for NexGen, v1 for Apex)
--   7.  policy_rules              :  4 rows (2 rules per policy version)
--   8.  drive_policy_assignments  :  2 rows (1 assignment per drive)
--   9.  evidence_records          :  8 rows (verified, pending, rejected)
--   10. evaluation_records        :  6 rows (ELIGIBLE, INELIGIBLE, UNVERIFIABLE)
--   11. rule_results              : 12 rows (granular evaluation results)
--   TOTAL                         : 50 rows
-- =================================================================

-- -----------------------------------------------------------------
-- 1. USERS (5 rows: Fictional Students, Password: Student@eqel2026)
-- -----------------------------------------------------------------
INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, created_at, updated_at)
SELECT 'demo.student01', 'aarav.sharma@demo.eqel.local', '$2a$10$3L3OwWYEy9UUAkmSYc0VbOrwNwa25FZlj1oNfB7V0So.NfOo60ye.', 'Aarav Sharma', r.id, TRUE, NOW(), NOW()
FROM roles r WHERE r.name = 'STUDENT'
AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'demo.student01');

INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, created_at, updated_at)
SELECT 'demo.student02', 'bhavna.patel@demo.eqel.local', '$2a$10$3L3OwWYEy9UUAkmSYc0VbOrwNwa25FZlj1oNfB7V0So.NfOo60ye.', 'Bhavna Patel', r.id, TRUE, NOW(), NOW()
FROM roles r WHERE r.name = 'STUDENT'
AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'demo.student02');

INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, created_at, updated_at)
SELECT 'demo.student03', 'chirag.verma@demo.eqel.local', '$2a$10$3L3OwWYEy9UUAkmSYc0VbOrwNwa25FZlj1oNfB7V0So.NfOo60ye.', 'Chirag Verma', r.id, TRUE, NOW(), NOW()
FROM roles r WHERE r.name = 'STUDENT'
AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'demo.student03');

INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, created_at, updated_at)
SELECT 'demo.student04', 'divya.nair@demo.eqel.local', '$2a$10$3L3OwWYEy9UUAkmSYc0VbOrwNwa25FZlj1oNfB7V0So.NfOo60ye.', 'Divya Nair', r.id, TRUE, NOW(), NOW()
FROM roles r WHERE r.name = 'STUDENT'
AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'demo.student04');

INSERT INTO users (username, email, password_hash, full_name, role_id, is_active, created_at, updated_at)
SELECT 'demo.student05', 'eshan.gupta@demo.eqel.local', '$2a$10$3L3OwWYEy9UUAkmSYc0VbOrwNwa25FZlj1oNfB7V0So.NfOo60ye.', 'Eshan Gupta', r.id, TRUE, NOW(), NOW()
FROM roles r WHERE r.name = 'STUDENT'
AND NOT EXISTS (SELECT 1 FROM users WHERE username = 'demo.student05');

-- -----------------------------------------------------------------
-- 2. STUDENT PROFILES (5 rows)
-- -----------------------------------------------------------------
INSERT INTO student_profiles (user_id, enrollment_number, department, program, semester, cgpa, backlogs, phone, date_of_birth, created_at, updated_at)
SELECT u.id, 'EQEL2026CS001', 'Computer Science', 'B.Tech', 7, 8.85, 0, '+91-98765-00001', '2004-05-15', NOW(), NOW()
FROM users u WHERE u.username = 'demo.student01'
AND NOT EXISTS (SELECT 1 FROM student_profiles WHERE enrollment_number = 'EQEL2026CS001');

INSERT INTO student_profiles (user_id, enrollment_number, department, program, semester, cgpa, backlogs, phone, date_of_birth, created_at, updated_at)
SELECT u.id, 'EQEL2026IT002', 'Information Technology', 'B.Tech', 7, 7.20, 0, '+91-98765-00002', '2004-08-22', NOW(), NOW()
FROM users u WHERE u.username = 'demo.student02'
AND NOT EXISTS (SELECT 1 FROM student_profiles WHERE enrollment_number = 'EQEL2026IT002');

INSERT INTO student_profiles (user_id, enrollment_number, department, program, semester, cgpa, backlogs, phone, date_of_birth, created_at, updated_at)
SELECT u.id, 'EQEL2026EC003', 'Electronics & Comm', 'B.Tech', 7, 8.10, 2, '+91-98765-00003', '2003-11-10', NOW(), NOW()
FROM users u WHERE u.username = 'demo.student03'
AND NOT EXISTS (SELECT 1 FROM student_profiles WHERE enrollment_number = 'EQEL2026EC003');

INSERT INTO student_profiles (user_id, enrollment_number, department, program, semester, cgpa, backlogs, phone, date_of_birth, created_at, updated_at)
SELECT u.id, 'EQEL2026CS004', 'Computer Science', 'B.Tech', 7, 8.40, 0, '+91-98765-00004', '2004-02-18', NOW(), NOW()
FROM users u WHERE u.username = 'demo.student04'
AND NOT EXISTS (SELECT 1 FROM student_profiles WHERE enrollment_number = 'EQEL2026CS004');

INSERT INTO student_profiles (user_id, enrollment_number, department, program, semester, cgpa, backlogs, phone, date_of_birth, created_at, updated_at)
SELECT u.id, 'EQEL2026ME005', 'Mechanical Engineering', 'B.Tech', 7, 7.90, 0, '+91-98765-00005', '2004-09-30', NOW(), NOW()
FROM users u WHERE u.username = 'demo.student05'
AND NOT EXISTS (SELECT 1 FROM student_profiles WHERE enrollment_number = 'EQEL2026ME005');

-- -----------------------------------------------------------------
-- 3. COMPANIES (2 rows)
-- -----------------------------------------------------------------
INSERT INTO companies (name, industry, website, description, is_active, created_by, created_at, updated_at)
SELECT 'NexGen Cloud Technologies', 'Cloud Computing & AI', 'https://nexgen.demo.local', 'Enterprise cloud native infrastructure and distributed AI compute platforms', TRUE, (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM companies WHERE name = 'NexGen Cloud Technologies');

INSERT INTO companies (name, industry, website, description, is_active, created_by, created_at, updated_at)
SELECT 'Apex Cybernetics Corp', 'Autonomous Systems & Robotics', 'https://apex-cyber.demo.local', 'Embedded real-time perception and autonomous systems architecture', TRUE, (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM companies WHERE name = 'Apex Cybernetics Corp');

-- -----------------------------------------------------------------
-- 4. PLACEMENT DRIVES (2 rows)
-- -----------------------------------------------------------------
INSERT INTO placement_drives (company_id, title, description, job_role, package_lpa, drive_status, registration_open, registration_close, drive_date, created_by, created_at, updated_at)
SELECT c.id, 'NexGen Cloud Software Engineer 2026', 'Full-time campus recruitment drive for NexGen Cloud infrastructure engineering teams', 'Associate Cloud Engineer', 14.50, 'OPEN', '2026-10-01 09:00:00', '2026-11-15 18:00:00', '2026-11-20', (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW(), NOW()
FROM companies c WHERE c.name = 'NexGen Cloud Technologies'
AND NOT EXISTS (SELECT 1 FROM placement_drives WHERE title = 'NexGen Cloud Software Engineer 2026');

INSERT INTO placement_drives (company_id, title, description, job_role, package_lpa, drive_status, registration_open, registration_close, drive_date, created_by, created_at, updated_at)
SELECT c.id, 'Apex Cybernetics Systems Engineer 2026', 'Graduate recruitment program for real-time systems, robotics firmware, and perception stacks', 'Systems Software Engineer', 18.00, 'OPEN', '2026-10-05 09:00:00', '2026-11-20 18:00:00', '2026-11-25', (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW(), NOW()
FROM companies c WHERE c.name = 'Apex Cybernetics Corp'
AND NOT EXISTS (SELECT 1 FROM placement_drives WHERE title = 'Apex Cybernetics Systems Engineer 2026');

-- -----------------------------------------------------------------
-- 5. ELIGIBILITY POLICIES (2 rows)
-- -----------------------------------------------------------------
INSERT INTO eligibility_policies (name, description, is_active, created_by, created_at, updated_at)
SELECT 'NexGen Engineering Eligibility Policy', 'Standard eligibility requirements for NexGen Cloud engineering roles', TRUE, (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM eligibility_policies WHERE name = 'NexGen Engineering Eligibility Policy');

INSERT INTO eligibility_policies (name, description, is_active, created_by, created_at, updated_at)
SELECT 'Apex Core Systems Eligibility Policy', 'Academic baseline criteria for Apex Cybernetics systems division', TRUE, (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM eligibility_policies WHERE name = 'Apex Core Systems Eligibility Policy');

-- -----------------------------------------------------------------
-- 6. POLICY VERSIONS (2 rows)
-- -----------------------------------------------------------------
INSERT INTO policy_versions (policy_id, version_number, notes, is_published, created_by, created_at)
SELECT p.id, 1, 'Initial published version for NexGen 2026 campus hiring', TRUE, (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW()
FROM eligibility_policies p WHERE p.name = 'NexGen Engineering Eligibility Policy'
AND NOT EXISTS (SELECT 1 FROM policy_versions pv WHERE pv.policy_id = p.id AND pv.version_number = 1);

INSERT INTO policy_versions (policy_id, version_number, notes, is_published, created_by, created_at)
SELECT p.id, 1, 'Initial published version for Apex Cybernetics 2026 drive', TRUE, (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW()
FROM eligibility_policies p WHERE p.name = 'Apex Core Systems Eligibility Policy'
AND NOT EXISTS (SELECT 1 FROM policy_versions pv WHERE pv.policy_id = p.id AND pv.version_number = 1);

-- -----------------------------------------------------------------
-- 7. POLICY RULES (4 rows: 2 for NexGen, 2 for Apex)
-- -----------------------------------------------------------------
INSERT INTO policy_rules (policy_version_id, rule_code, rule_description, field_name, operator, threshold_value, is_mandatory, created_at)
SELECT pv.id, 'NG-MIN-CGPA', 'Minimum cumulative GPA of 7.50 required', 'CGPA', 'GTE', '7.50', TRUE, NOW()
FROM policy_versions pv
JOIN eligibility_policies p ON pv.policy_id = p.id
WHERE p.name = 'NexGen Engineering Eligibility Policy' AND pv.version_number = 1
AND NOT EXISTS (SELECT 1 FROM policy_rules pr WHERE pr.policy_version_id = pv.id AND pr.rule_code = 'NG-MIN-CGPA');

INSERT INTO policy_rules (policy_version_id, rule_code, rule_description, field_name, operator, threshold_value, is_mandatory, created_at)
SELECT pv.id, 'NG-MAX-BACKLOG', 'Maximum permitted active backlogs is zero', 'BACKLOGS', 'LTE', '0', TRUE, NOW()
FROM policy_versions pv
JOIN eligibility_policies p ON pv.policy_id = p.id
WHERE p.name = 'NexGen Engineering Eligibility Policy' AND pv.version_number = 1
AND NOT EXISTS (SELECT 1 FROM policy_rules pr WHERE pr.policy_version_id = pv.id AND pr.rule_code = 'NG-MAX-BACKLOG');

INSERT INTO policy_rules (policy_version_id, rule_code, rule_description, field_name, operator, threshold_value, is_mandatory, created_at)
SELECT pv.id, 'AC-MIN-CGPA', 'Minimum cumulative GPA of 8.00 required for systems division', 'CGPA', 'GTE', '8.00', TRUE, NOW()
FROM policy_versions pv
JOIN eligibility_policies p ON pv.policy_id = p.id
WHERE p.name = 'Apex Core Systems Eligibility Policy' AND pv.version_number = 1
AND NOT EXISTS (SELECT 1 FROM policy_rules pr WHERE pr.policy_version_id = pv.id AND pr.rule_code = 'AC-MIN-CGPA');

INSERT INTO policy_rules (policy_version_id, rule_code, rule_description, field_name, operator, threshold_value, is_mandatory, created_at)
SELECT pv.id, 'AC-MAX-BACKLOG', 'No active backlogs allowed', 'BACKLOGS', 'LTE', '0', TRUE, NOW()
FROM policy_versions pv
JOIN eligibility_policies p ON pv.policy_id = p.id
WHERE p.name = 'Apex Core Systems Eligibility Policy' AND pv.version_number = 1
AND NOT EXISTS (SELECT 1 FROM policy_rules pr WHERE pr.policy_version_id = pv.id AND pr.rule_code = 'AC-MAX-BACKLOG');

-- -----------------------------------------------------------------
-- 8. DRIVE POLICY ASSIGNMENTS (2 rows)
-- -----------------------------------------------------------------
INSERT INTO drive_policy_assignments (drive_id, policy_version_id, assigned_by, assigned_at)
SELECT d.id, pv.id, (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW()
FROM placement_drives d
CROSS JOIN policy_versions pv
JOIN eligibility_policies p ON pv.policy_id = p.id
WHERE d.title = 'NexGen Cloud Software Engineer 2026'
  AND p.name = 'NexGen Engineering Eligibility Policy'
  AND pv.version_number = 1
  AND NOT EXISTS (SELECT 1 FROM drive_policy_assignments dpa WHERE dpa.drive_id = d.id AND dpa.policy_version_id = pv.id);

INSERT INTO drive_policy_assignments (drive_id, policy_version_id, assigned_by, assigned_at)
SELECT d.id, pv.id, (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW()
FROM placement_drives d
CROSS JOIN policy_versions pv
JOIN eligibility_policies p ON pv.policy_id = p.id
WHERE d.title = 'Apex Cybernetics Systems Engineer 2026'
  AND p.name = 'Apex Core Systems Eligibility Policy'
  AND pv.version_number = 1
  AND NOT EXISTS (SELECT 1 FROM drive_policy_assignments dpa WHERE dpa.drive_id = d.id AND dpa.policy_version_id = pv.id);

-- -----------------------------------------------------------------
-- 9. EVIDENCE RECORDS (8 rows)
-- -----------------------------------------------------------------
-- Student 1 (Aarav): Verified CGPA
INSERT INTO evidence_records (student_id, evidence_type, field_name, field_value, status, revision, source, submitted_at, verified_at, verified_by, reviewer_notes)
SELECT sp.id, 'OFFICIAL_TRANSCRIPT', 'CGPA', '8.85', 'VERIFIED', 1, 'University Examination Branch Portal', NOW(), NOW(), (SELECT id FROM users WHERE username = 'admin' LIMIT 1), 'Authenticated against controller of examinations database'
FROM student_profiles sp WHERE sp.enrollment_number = 'EQEL2026CS001'
AND NOT EXISTS (SELECT 1 FROM evidence_records er WHERE er.student_id = sp.id AND er.evidence_type = 'OFFICIAL_TRANSCRIPT' AND er.field_name = 'CGPA');

-- Student 1 (Aarav): Verified Backlogs
INSERT INTO evidence_records (student_id, evidence_type, field_name, field_value, status, revision, source, submitted_at, verified_at, verified_by, reviewer_notes)
SELECT sp.id, 'ACADEMIC_RECORD', 'BACKLOGS', '0', 'VERIFIED', 1, 'Institutional ERP Grade Card', NOW(), NOW(), (SELECT id FROM users WHERE username = 'admin' LIMIT 1), 'Zero arrears confirmed for semesters 1 through 6'
FROM student_profiles sp WHERE sp.enrollment_number = 'EQEL2026CS001'
AND NOT EXISTS (SELECT 1 FROM evidence_records er WHERE er.student_id = sp.id AND er.evidence_type = 'ACADEMIC_RECORD' AND er.field_name = 'BACKLOGS');

-- Student 2 (Bhavna): Verified CGPA (7.20 - Ineligible for 7.50 requirement)
INSERT INTO evidence_records (student_id, evidence_type, field_name, field_value, status, revision, source, submitted_at, verified_at, verified_by, reviewer_notes)
SELECT sp.id, 'OFFICIAL_TRANSCRIPT', 'CGPA', '7.20', 'VERIFIED', 1, 'Department Registrar Sealed Copy', NOW(), NOW(), (SELECT id FROM users WHERE username = 'admin' LIMIT 1), 'Transcript certified by registrar office'
FROM student_profiles sp WHERE sp.enrollment_number = 'EQEL2026IT002'
AND NOT EXISTS (SELECT 1 FROM evidence_records er WHERE er.student_id = sp.id AND er.evidence_type = 'OFFICIAL_TRANSCRIPT' AND er.field_name = 'CGPA');

-- Student 3 (Chirag): Verified CGPA (8.10)
INSERT INTO evidence_records (student_id, evidence_type, field_name, field_value, status, revision, source, submitted_at, verified_at, verified_by, reviewer_notes)
SELECT sp.id, 'OFFICIAL_TRANSCRIPT', 'CGPA', '8.10', 'VERIFIED', 1, 'Digital Transcript Service', NOW(), NOW(), (SELECT id FROM users WHERE username = 'admin' LIMIT 1), 'Digital signature validated'
FROM student_profiles sp WHERE sp.enrollment_number = 'EQEL2026EC003'
AND NOT EXISTS (SELECT 1 FROM evidence_records er WHERE er.student_id = sp.id AND er.evidence_type = 'OFFICIAL_TRANSCRIPT' AND er.field_name = 'CGPA');

-- Student 3 (Chirag): Verified Backlogs (2 - Ineligible for 0 requirement)
INSERT INTO evidence_records (student_id, evidence_type, field_name, field_value, status, revision, source, submitted_at, verified_at, verified_by, reviewer_notes)
SELECT sp.id, 'BACKLOG_SUMMARY', 'BACKLOGS', '2', 'VERIFIED', 1, 'Department Arrear Ledger', NOW(), NOW(), (SELECT id FROM users WHERE username = 'admin' LIMIT 1), 'Confirmed 2 active arrears in EC302 and EC305'
FROM student_profiles sp WHERE sp.enrollment_number = 'EQEL2026EC003'
AND NOT EXISTS (SELECT 1 FROM evidence_records er WHERE er.student_id = sp.id AND er.evidence_type = 'BACKLOG_SUMMARY' AND er.field_name = 'BACKLOGS');

-- Student 4 (Divya): Pending CGPA (Awaiting officer review -> Unverifiable)
INSERT INTO evidence_records (student_id, evidence_type, field_name, field_value, status, revision, source, submitted_at, verified_at, verified_by, reviewer_notes)
SELECT sp.id, 'OFFICIAL_TRANSCRIPT', 'CGPA', '8.40', 'PENDING', 1, 'Student Self-Upload (PDF)', NOW(), NULL, NULL, 'Pending officer review queue'
FROM student_profiles sp WHERE sp.enrollment_number = 'EQEL2026CS004'
AND NOT EXISTS (SELECT 1 FROM evidence_records er WHERE er.student_id = sp.id AND er.evidence_type = 'OFFICIAL_TRANSCRIPT' AND er.field_name = 'CGPA');

-- Student 5 (Eshan): Rejected CGPA (Uncertified mobile scan -> Unverifiable)
INSERT INTO evidence_records (student_id, evidence_type, field_name, field_value, status, revision, source, submitted_at, verified_at, verified_by, reviewer_notes)
SELECT sp.id, 'OFFICIAL_TRANSCRIPT', 'CGPA', '7.90', 'REJECTED', 1, 'Uncertified Mobile Scan', NOW(), NOW(), (SELECT id FROM users WHERE username = 'admin' LIMIT 1), 'Illegible photocopy; missing registrar seal and QR validation code'
FROM student_profiles sp WHERE sp.enrollment_number = 'EQEL2026ME005'
AND NOT EXISTS (SELECT 1 FROM evidence_records er WHERE er.student_id = sp.id AND er.evidence_type = 'OFFICIAL_TRANSCRIPT' AND er.field_name = 'CGPA');

-- Student 5 (Eshan): Verified Certificate (Non-academic skill credential)
INSERT INTO evidence_records (student_id, evidence_type, field_name, field_value, status, revision, source, submitted_at, verified_at, verified_by, reviewer_notes)
SELECT sp.id, 'CERTIFICATE', 'SKILLS', 'CAD, Python, FEA', 'VERIFIED', 1, 'National Skill Development Academy', NOW(), NOW(), (SELECT id FROM users WHERE username = 'admin' LIMIT 1), 'Certificate credential verified online'
FROM student_profiles sp WHERE sp.enrollment_number = 'EQEL2026ME005'
AND NOT EXISTS (SELECT 1 FROM evidence_records er WHERE er.student_id = sp.id AND er.evidence_type = 'CERTIFICATE' AND er.field_name = 'SKILLS');

-- -----------------------------------------------------------------
-- 10. EVALUATION RECORDS (6 rows)
-- -----------------------------------------------------------------
-- Eval 1: Student 1 vs NexGen (ELIGIBLE)
INSERT INTO evaluation_records (student_id, drive_id, policy_version_id, overall_outcome, explanation, decision_engine_version, evaluated_by, evaluated_at)
SELECT sp.id, d.id, pv.id, 'ELIGIBLE', 'ELIGIBLE: All 2 mandatory rule(s) fully satisfied by verified evidence.', '1.0.0', (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW()
FROM student_profiles sp
CROSS JOIN placement_drives d
CROSS JOIN policy_versions pv
JOIN eligibility_policies p ON pv.policy_id = p.id
WHERE sp.enrollment_number = 'EQEL2026CS001'
  AND d.title = 'NexGen Cloud Software Engineer 2026'
  AND p.name = 'NexGen Engineering Eligibility Policy'
  AND pv.version_number = 1
  AND NOT EXISTS (SELECT 1 FROM evaluation_records er WHERE er.student_id = sp.id AND er.drive_id = d.id);

-- Eval 2: Student 1 vs Apex (ELIGIBLE)
INSERT INTO evaluation_records (student_id, drive_id, policy_version_id, overall_outcome, explanation, decision_engine_version, evaluated_by, evaluated_at)
SELECT sp.id, d.id, pv.id, 'ELIGIBLE', 'ELIGIBLE: All 2 mandatory rule(s) fully satisfied by verified evidence.', '1.0.0', (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW()
FROM student_profiles sp
CROSS JOIN placement_drives d
CROSS JOIN policy_versions pv
JOIN eligibility_policies p ON pv.policy_id = p.id
WHERE sp.enrollment_number = 'EQEL2026CS001'
  AND d.title = 'Apex Cybernetics Systems Engineer 2026'
  AND p.name = 'Apex Core Systems Eligibility Policy'
  AND pv.version_number = 1
  AND NOT EXISTS (SELECT 1 FROM evaluation_records er WHERE er.student_id = sp.id AND er.drive_id = d.id);

-- Eval 3: Student 2 vs NexGen (INELIGIBLE: CGPA 7.20 < 7.50)
INSERT INTO evaluation_records (student_id, drive_id, policy_version_id, overall_outcome, explanation, decision_engine_version, evaluated_by, evaluated_at)
SELECT sp.id, d.id, pv.id, 'INELIGIBLE', 'INELIGIBLE: Violates 1 mandatory rule(s). Deterministic disqualification.', '1.0.0', (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW()
FROM student_profiles sp
CROSS JOIN placement_drives d
CROSS JOIN policy_versions pv
JOIN eligibility_policies p ON pv.policy_id = p.id
WHERE sp.enrollment_number = 'EQEL2026IT002'
  AND d.title = 'NexGen Cloud Software Engineer 2026'
  AND p.name = 'NexGen Engineering Eligibility Policy'
  AND pv.version_number = 1
  AND NOT EXISTS (SELECT 1 FROM evaluation_records er WHERE er.student_id = sp.id AND er.drive_id = d.id);

-- Eval 4: Student 3 vs NexGen (INELIGIBLE: Backlogs 2 > 0)
INSERT INTO evaluation_records (student_id, drive_id, policy_version_id, overall_outcome, explanation, decision_engine_version, evaluated_by, evaluated_at)
SELECT sp.id, d.id, pv.id, 'INELIGIBLE', 'INELIGIBLE: Violates 1 mandatory rule(s). Deterministic disqualification.', '1.0.0', (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW()
FROM student_profiles sp
CROSS JOIN placement_drives d
CROSS JOIN policy_versions pv
JOIN eligibility_policies p ON pv.policy_id = p.id
WHERE sp.enrollment_number = 'EQEL2026EC003'
  AND d.title = 'NexGen Cloud Software Engineer 2026'
  AND p.name = 'NexGen Engineering Eligibility Policy'
  AND pv.version_number = 1
  AND NOT EXISTS (SELECT 1 FROM evaluation_records er WHERE er.student_id = sp.id AND er.drive_id = d.id);

-- Eval 5: Student 4 vs NexGen (UNVERIFIABLE: CGPA evidence pending)
INSERT INTO evaluation_records (student_id, drive_id, policy_version_id, overall_outcome, explanation, decision_engine_version, evaluated_by, evaluated_at)
SELECT sp.id, d.id, pv.id, 'UNVERIFIABLE', 'UNVERIFIABLE: 1 mandatory rule(s) cannot be confirmed due to missing, pending, or conflicting evidence.', '1.0.0', (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW()
FROM student_profiles sp
CROSS JOIN placement_drives d
CROSS JOIN policy_versions pv
JOIN eligibility_policies p ON pv.policy_id = p.id
WHERE sp.enrollment_number = 'EQEL2026CS004'
  AND d.title = 'NexGen Cloud Software Engineer 2026'
  AND p.name = 'NexGen Engineering Eligibility Policy'
  AND pv.version_number = 1
  AND NOT EXISTS (SELECT 1 FROM evaluation_records er WHERE er.student_id = sp.id AND er.drive_id = d.id);

-- Eval 6: Student 5 vs NexGen (UNVERIFIABLE: CGPA evidence rejected/unverified)
INSERT INTO evaluation_records (student_id, drive_id, policy_version_id, overall_outcome, explanation, decision_engine_version, evaluated_by, evaluated_at)
SELECT sp.id, d.id, pv.id, 'UNVERIFIABLE', 'UNVERIFIABLE: 1 mandatory rule(s) cannot be confirmed due to missing, pending, or conflicting evidence.', '1.0.0', (SELECT id FROM users WHERE username = 'admin' LIMIT 1), NOW()
FROM student_profiles sp
CROSS JOIN placement_drives d
CROSS JOIN policy_versions pv
JOIN eligibility_policies p ON pv.policy_id = p.id
WHERE sp.enrollment_number = 'EQEL2026ME005'
  AND d.title = 'NexGen Cloud Software Engineer 2026'
  AND p.name = 'NexGen Engineering Eligibility Policy'
  AND pv.version_number = 1
  AND NOT EXISTS (SELECT 1 FROM evaluation_records er WHERE er.student_id = sp.id AND er.drive_id = d.id);

-- -----------------------------------------------------------------
-- 11. RULE RESULTS (12 rows: 2 per evaluation)
-- -----------------------------------------------------------------
-- Eval 1 (Student 1 vs NexGen: SATISFIED, SATISFIED)
INSERT INTO rule_results (evaluation_id, rule_id, rule_code, result, evidence_value, threshold_value, explanation)
SELECT ev.id, pr.id, pr.rule_code, 'SATISFIED', '8.85', '7.50', 'Satisfies rule: CGPA GTE 7.50 (actual: 8.85)'
FROM evaluation_records ev
JOIN student_profiles sp ON ev.student_id = sp.id
JOIN placement_drives d ON ev.drive_id = d.id
JOIN policy_rules pr ON pr.policy_version_id = ev.policy_version_id AND pr.rule_code = 'NG-MIN-CGPA'
WHERE sp.enrollment_number = 'EQEL2026CS001' AND d.title = 'NexGen Cloud Software Engineer 2026'
AND NOT EXISTS (SELECT 1 FROM rule_results rr WHERE rr.evaluation_id = ev.id AND rr.rule_id = pr.id);

INSERT INTO rule_results (evaluation_id, rule_id, rule_code, result, evidence_value, threshold_value, explanation)
SELECT ev.id, pr.id, pr.rule_code, 'SATISFIED', '0', '0', 'Satisfies rule: BACKLOGS LTE 0 (actual: 0)'
FROM evaluation_records ev
JOIN student_profiles sp ON ev.student_id = sp.id
JOIN placement_drives d ON ev.drive_id = d.id
JOIN policy_rules pr ON pr.policy_version_id = ev.policy_version_id AND pr.rule_code = 'NG-MAX-BACKLOG'
WHERE sp.enrollment_number = 'EQEL2026CS001' AND d.title = 'NexGen Cloud Software Engineer 2026'
AND NOT EXISTS (SELECT 1 FROM rule_results rr WHERE rr.evaluation_id = ev.id AND rr.rule_id = pr.id);

-- Eval 2 (Student 1 vs Apex: SATISFIED, SATISFIED)
INSERT INTO rule_results (evaluation_id, rule_id, rule_code, result, evidence_value, threshold_value, explanation)
SELECT ev.id, pr.id, pr.rule_code, 'SATISFIED', '8.85', '8.00', 'Satisfies rule: CGPA GTE 8.00 (actual: 8.85)'
FROM evaluation_records ev
JOIN student_profiles sp ON ev.student_id = sp.id
JOIN placement_drives d ON ev.drive_id = d.id
JOIN policy_rules pr ON pr.policy_version_id = ev.policy_version_id AND pr.rule_code = 'AC-MIN-CGPA'
WHERE sp.enrollment_number = 'EQEL2026CS001' AND d.title = 'Apex Cybernetics Systems Engineer 2026'
AND NOT EXISTS (SELECT 1 FROM rule_results rr WHERE rr.evaluation_id = ev.id AND rr.rule_id = pr.id);

INSERT INTO rule_results (evaluation_id, rule_id, rule_code, result, evidence_value, threshold_value, explanation)
SELECT ev.id, pr.id, pr.rule_code, 'SATISFIED', '0', '0', 'Satisfies rule: BACKLOGS LTE 0 (actual: 0)'
FROM evaluation_records ev
JOIN student_profiles sp ON ev.student_id = sp.id
JOIN placement_drives d ON ev.drive_id = d.id
JOIN policy_rules pr ON pr.policy_version_id = ev.policy_version_id AND pr.rule_code = 'AC-MAX-BACKLOG'
WHERE sp.enrollment_number = 'EQEL2026CS001' AND d.title = 'Apex Cybernetics Systems Engineer 2026'
AND NOT EXISTS (SELECT 1 FROM rule_results rr WHERE rr.evaluation_id = ev.id AND rr.rule_id = pr.id);

-- Eval 3 (Student 2 vs NexGen: VIOLATED, SATISFIED)
INSERT INTO rule_results (evaluation_id, rule_id, rule_code, result, evidence_value, threshold_value, explanation)
SELECT ev.id, pr.id, pr.rule_code, 'VIOLATED', '7.20', '7.50', 'Violates rule: requires CGPA GTE 7.50, but actual value is 7.20'
FROM evaluation_records ev
JOIN student_profiles sp ON ev.student_id = sp.id
JOIN placement_drives d ON ev.drive_id = d.id
JOIN policy_rules pr ON pr.policy_version_id = ev.policy_version_id AND pr.rule_code = 'NG-MIN-CGPA'
WHERE sp.enrollment_number = 'EQEL2026IT002' AND d.title = 'NexGen Cloud Software Engineer 2026'
AND NOT EXISTS (SELECT 1 FROM rule_results rr WHERE rr.evaluation_id = ev.id AND rr.rule_id = pr.id);

INSERT INTO rule_results (evaluation_id, rule_id, rule_code, result, evidence_value, threshold_value, explanation)
SELECT ev.id, pr.id, pr.rule_code, 'SATISFIED', '0', '0', 'Satisfies rule: BACKLOGS LTE 0 (actual: 0)'
FROM evaluation_records ev
JOIN student_profiles sp ON ev.student_id = sp.id
JOIN placement_drives d ON ev.drive_id = d.id
JOIN policy_rules pr ON pr.policy_version_id = ev.policy_version_id AND pr.rule_code = 'NG-MAX-BACKLOG'
WHERE sp.enrollment_number = 'EQEL2026IT002' AND d.title = 'NexGen Cloud Software Engineer 2026'
AND NOT EXISTS (SELECT 1 FROM rule_results rr WHERE rr.evaluation_id = ev.id AND rr.rule_id = pr.id);

-- Eval 4 (Student 3 vs NexGen: SATISFIED, VIOLATED)
INSERT INTO rule_results (evaluation_id, rule_id, rule_code, result, evidence_value, threshold_value, explanation)
SELECT ev.id, pr.id, pr.rule_code, 'SATISFIED', '8.10', '7.50', 'Satisfies rule: CGPA GTE 7.50 (actual: 8.10)'
FROM evaluation_records ev
JOIN student_profiles sp ON ev.student_id = sp.id
JOIN placement_drives d ON ev.drive_id = d.id
JOIN policy_rules pr ON pr.policy_version_id = ev.policy_version_id AND pr.rule_code = 'NG-MIN-CGPA'
WHERE sp.enrollment_number = 'EQEL2026EC003' AND d.title = 'NexGen Cloud Software Engineer 2026'
AND NOT EXISTS (SELECT 1 FROM rule_results rr WHERE rr.evaluation_id = ev.id AND rr.rule_id = pr.id);

INSERT INTO rule_results (evaluation_id, rule_id, rule_code, result, evidence_value, threshold_value, explanation)
SELECT ev.id, pr.id, pr.rule_code, 'VIOLATED', '2', '0', 'Violates rule: requires BACKLOGS LTE 0, but actual value is 2'
FROM evaluation_records ev
JOIN student_profiles sp ON ev.student_id = sp.id
JOIN placement_drives d ON ev.drive_id = d.id
JOIN policy_rules pr ON pr.policy_version_id = ev.policy_version_id AND pr.rule_code = 'NG-MAX-BACKLOG'
WHERE sp.enrollment_number = 'EQEL2026EC003' AND d.title = 'NexGen Cloud Software Engineer 2026'
AND NOT EXISTS (SELECT 1 FROM rule_results rr WHERE rr.evaluation_id = ev.id AND rr.rule_id = pr.id);

-- Eval 5 (Student 4 vs NexGen: UNVERIFIABLE, SATISFIED)
INSERT INTO rule_results (evaluation_id, rule_id, rule_code, result, evidence_value, threshold_value, explanation)
SELECT ev.id, pr.id, pr.rule_code, 'UNVERIFIABLE', '8.40 (UNVERIFIED)', '7.50', 'Evidence submitted for CGPA is awaiting officer verification'
FROM evaluation_records ev
JOIN student_profiles sp ON ev.student_id = sp.id
JOIN placement_drives d ON ev.drive_id = d.id
JOIN policy_rules pr ON pr.policy_version_id = ev.policy_version_id AND pr.rule_code = 'NG-MIN-CGPA'
WHERE sp.enrollment_number = 'EQEL2026CS004' AND d.title = 'NexGen Cloud Software Engineer 2026'
AND NOT EXISTS (SELECT 1 FROM rule_results rr WHERE rr.evaluation_id = ev.id AND rr.rule_id = pr.id);

INSERT INTO rule_results (evaluation_id, rule_id, rule_code, result, evidence_value, threshold_value, explanation)
SELECT ev.id, pr.id, pr.rule_code, 'SATISFIED', '0', '0', 'Satisfies rule: BACKLOGS LTE 0 (actual: 0)'
FROM evaluation_records ev
JOIN student_profiles sp ON ev.student_id = sp.id
JOIN placement_drives d ON ev.drive_id = d.id
JOIN policy_rules pr ON pr.policy_version_id = ev.policy_version_id AND pr.rule_code = 'NG-MAX-BACKLOG'
WHERE sp.enrollment_number = 'EQEL2026CS004' AND d.title = 'NexGen Cloud Software Engineer 2026'
AND NOT EXISTS (SELECT 1 FROM rule_results rr WHERE rr.evaluation_id = ev.id AND rr.rule_id = pr.id);

-- Eval 6 (Student 5 vs NexGen: UNVERIFIABLE, SATISFIED)
INSERT INTO rule_results (evaluation_id, rule_id, rule_code, result, evidence_value, threshold_value, explanation)
SELECT ev.id, pr.id, pr.rule_code, 'UNVERIFIABLE', '7.90 (UNVERIFIED)', '7.50', 'Evidence submitted for CGPA is awaiting officer verification'
FROM evaluation_records ev
JOIN student_profiles sp ON ev.student_id = sp.id
JOIN placement_drives d ON ev.drive_id = d.id
JOIN policy_rules pr ON pr.policy_version_id = ev.policy_version_id AND pr.rule_code = 'NG-MIN-CGPA'
WHERE sp.enrollment_number = 'EQEL2026ME005' AND d.title = 'NexGen Cloud Software Engineer 2026'
AND NOT EXISTS (SELECT 1 FROM rule_results rr WHERE rr.evaluation_id = ev.id AND rr.rule_id = pr.id);

INSERT INTO rule_results (evaluation_id, rule_id, rule_code, result, evidence_value, threshold_value, explanation)
SELECT ev.id, pr.id, pr.rule_code, 'SATISFIED', '0', '0', 'Satisfies rule: BACKLOGS LTE 0 (actual: 0)'
FROM evaluation_records ev
JOIN student_profiles sp ON ev.student_id = sp.id
JOIN placement_drives d ON ev.drive_id = d.id
JOIN policy_rules pr ON pr.policy_version_id = ev.policy_version_id AND pr.rule_code = 'NG-MAX-BACKLOG'
WHERE sp.enrollment_number = 'EQEL2026ME005' AND d.title = 'NexGen Cloud Software Engineer 2026'
AND NOT EXISTS (SELECT 1 FROM rule_results rr WHERE rr.evaluation_id = ev.id AND rr.rule_id = pr.id);
