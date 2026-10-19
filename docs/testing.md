# EQEL — Testing Strategy and Verification Plan

## 1. Overview
EQEL enforces automated test verification across both backend and frontend layers:
1. **Frontend Verification**: TypeScript type validation, component rendering tests, responsive design checks, and end-to-end user navigation flows.
2. **Backend Verification**: Unit tests for the Eligibility Engine, Spring Security integration tests, API contract tests, and Flyway migration tests.

## 2. Test Execution Commands
- **Frontend Build & Typecheck**:
  ```bash
  cd frontend
  npm run build
  ```
- **Backend Test Suite**:
  ```bash
  cd backend
  mvn test
  ```
- **Backend Packaging & Verification**:
  ```bash
  cd backend
  mvn clean verify
  ```

## 3. Eligibility Engine Test Coverage (Mandatory Scenarios)
The core decision engine (`com.eqel.engine.EligibilityEngine`) is validated against all admissible criteria:
1. **Fully Satisfied (`ELIGIBLE`)**:
   - All mandatory criteria (e.g., `CGPA >= 7.5`, `BACKLOGS == 0`, `DEPT IN [CSE, ECE]`) are met by verified evidence.
2. **Deterministic Violation (`INELIGIBLE`)**:
   - At least one mandatory criterion is definitively violated by verified evidence (e.g. `CGPA = 6.2 < 7.5`).
3. **Insufficient Evidence (`UNVERIFIABLE`)**:
   - Mandatory evidence field is missing or unverified. Missing evidence does not automatically imply ineligibility.
4. **Conflicting Evidence (`UNVERIFIABLE`)**:
   - Evidence records contain contradictory values (e.g. transcript claims `CGPA = 8.1` but manual submission claims `7.2`).
5. **Non-mandatory Rules**:
   - Optional rules produce warnings or secondary flags without overturning an otherwise eligible outcome.
6. **Policy Version Immutability**:
   - Re-evaluating a past decision against the exact historical policy version yields an identical verdict.
