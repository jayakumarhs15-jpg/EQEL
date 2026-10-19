-- V2__seed_roles.sql — Insert the three mandatory roles
INSERT INTO roles (name, description) VALUES
    ('ADMIN',             'System administrator — manages users, roles, audit, system settings'),
    ('PLACEMENT_OFFICER', 'Placement officer — manages students, companies, drives, policies, evaluations'),
    ('STUDENT',           'Student — views profile, evidence, eligibility, drives, applications');
