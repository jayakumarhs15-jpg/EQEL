/* EQEL Core Type Definitions */

// ── Roles ──
export type Role = 'ADMIN' | 'PLACEMENT_OFFICER' | 'STUDENT';

// ── Auth ──
export interface LoginRequest {
  username: string;
  password: string;
  role: Role;
}

export interface AuthUser {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: Role;
  isActive: boolean;
}

export interface AuthState {
  user: AuthUser | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

// ── Users ──
export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: Role;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CreateUserRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
  role: Role;
}

// ── Student Profile ──
export interface StudentProfile {
  id: number;
  userId: number;
  enrollmentNumber: string;
  department: string;
  program: string;
  semester: number;
  cgpa: number;
  backlogs: number;
  phone: string;
  dateOfBirth: string;
  fullName: string;
  email: string;
}

// ── Company ──
export interface Company {
  id: number;
  name: string;
  industry: string;
  website: string;
  description: string;
  isActive: boolean;
  createdAt: string;
}

// ── Placement Drive ──
export type DriveStatus = 'DRAFT' | 'OPEN' | 'CLOSED' | 'COMPLETED' | 'CANCELLED';

export interface PlacementDrive {
  id: number;
  companyId: number;
  companyName: string;
  title: string;
  description: string;
  jobRole: string;
  packageLpa: number;
  driveStatus: DriveStatus;
  registrationOpen: string;
  registrationClose: string;
  driveDate: string;
  createdAt: string;
}

// ── Eligibility Policy ──
export interface EligibilityPolicy {
  id: number;
  name: string;
  description: string;
  isActive: boolean;
  createdAt: string;
}

export interface PolicyVersion {
  id: number;
  policyId: number;
  versionNumber: number;
  notes: string;
  isPublished: boolean;
  rules: PolicyRule[];
  createdAt: string;
}

export type RuleOperator = 'GTE' | 'LTE' | 'EQ' | 'NEQ' | 'GT' | 'LT' | 'IN' | 'NOT_IN';

export interface PolicyRule {
  id: number;
  policyVersionId: number;
  ruleCode: string;
  ruleDescription: string;
  fieldName: string;
  operator: RuleOperator;
  thresholdValue: string;
  isMandatory: boolean;
}

// ── Evidence ──
export type EvidenceStatus = 'PENDING' | 'VERIFIED' | 'REJECTED' | 'CONFLICTING';

export interface EvidenceRecord {
  id: number;
  studentId: number;
  evidenceType: string;
  fieldName: string;
  fieldValue: string;
  status: EvidenceStatus;
  revision: number;
  source: string;
  submittedAt: string;
  verifiedAt: string | null;
  verifiedBy: number | null;
  reviewerNotes: string | null;
}

// ── Evaluation ──
export type OverallOutcome = 'ELIGIBLE' | 'INELIGIBLE' | 'UNVERIFIABLE';
export type RuleResult = 'SATISFIED' | 'VIOLATED' | 'UNVERIFIABLE';

export interface EvaluationRecord {
  id: number;
  studentId: number;
  driveId: number;
  policyVersionId: number;
  overallOutcome: OverallOutcome;
  explanation: string;
  decisionEngineVersion: string;
  evaluatedAt: string;
  ruleResults: RuleResultRecord[];
}

export interface RuleResultRecord {
  id: number;
  evaluationId: number;
  ruleId: number;
  ruleCode: string;
  result: RuleResult;
  evidenceValue: string | null;
  thresholdValue: string;
  explanation: string;
}

// ── Application ──
export type ApplicationStatus = 'APPLIED' | 'SHORTLISTED' | 'SELECTED' | 'REJECTED' | 'WITHDRAWN';

export interface Application {
  id: number;
  studentId: number;
  driveId: number;
  status: ApplicationStatus;
  appliedAt: string;
  driveName?: string;
  companyName?: string;
}

// ── Audit ──
export interface AuditEvent {
  id: number;
  userId: number;
  username: string;
  action: string;
  entityType: string;
  entityId: number;
  details: string;
  ipAddress: string;
  createdAt: string;
}

// ── Pagination ──
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

// ── API Error ──
export interface ApiError {
  status: number;
  message: string;
  errors?: Record<string, string>;
}
