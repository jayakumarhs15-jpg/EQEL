# EQEL — COMPLETE APPLICATION FROM ZERO
## Antigravity Master Build Prompt

## 1. Initial Setup Instructions

I am starting EQEL from zero in an empty project folder.

1. Read this entire `EQEL_MASTER_PROMPT.md` before implementation.
2. Create the frontend, backend, database configuration, tests, and documentation from scratch.
3. Use React + TypeScript + Vite, Spring Boot + Spring Security, and MySQL.
4. First inspect the workspace and available development tools.
5. For the cinematic landing page, check whether `frontend/public/videos/kingfisher-hero.mp4` exists.
6. If the local video exists, use it. Otherwise, use the remote URL `https://thinkingods.com/demos/kingfisher-hero/hero.mp4` directly. Do not repeatedly retry failed downloads.
7. Implement a sage-green fallback background and reveal all essential page content if video playback fails.
8. Build the application in phases, testing each phase before proceeding.
9. Do not stop after creating folders, a plan, or a static UI. Implement functional APIs, authentication, database persistence, and eligibility logic.
10. Do not invent test results or claim unfinished features are complete.
11. Start implementation immediately and document any external dependencies that require my action.

If the workspace is empty, initialize the project. If existing files are present, inspect them and preserve useful work rather than overwriting it blindly.

---

## 2. Your Role

Act as a senior full-stack engineer, software architect, UI/UX designer, database engineer, security engineer, and QA engineer.

Build a complete, functional application named **EQEL — Evidence-Qualified Eligibility Ledger**.

Work directly in the project workspace. Implement, run, test, debug, and document the actual application.

Priorities:

1. Correct eligibility decisions.
2. Secure authentication and authorization.
3. Reliable database persistence and historical traceability.
4. Functional workflows across all three roles.
5. Consistent, premium UI/UX.
6. Automated testing and verified execution.

Never claim that functionality works unless it has been tested.

---

## 3. Project Purpose

EQEL is an evidence-aware campus placement eligibility system.

It evaluates student academic evidence against company-specific, versioned placement policies and records explainable eligibility decisions.

The system must explain:

- Which placement rules apply to a student.
- Which evidence supports each rule.
- Which rules passed, failed, or remain unverifiable.
- Why a particular overall outcome was produced.
- Which policy version and evidence revisions were used.
- What changes when evidence or a policy is updated.

Historical decisions must remain traceable to their original inputs and decision-engine version.

## 4. Mandatory Eligibility Semantics

Implement exactly three overall outcomes.

**ELIGIBLE**

Every admissible evidence scenario represented by the implemented evidence model satisfies all mandatory eligibility rules.

**INELIGIBLE**

Every admissible evidence scenario represented by the implemented evidence model violates at least one mandatory eligibility rule.

**UNVERIFIABLE**

The available evidence does not establish one consistent overall outcome across the admissible scenarios.

Rules:

- Missing evidence must not automatically mean Ineligible.
- Do not invent eligibility thresholds.
- Placement Officers configure the rules for each company and placement drive.
- Distinguish verified, missing, conflicting, and unsupported evidence.
- Each evaluation must record its student, drive, policy version, evidence revisions or immutable snapshot, rule-level results, overall outcome, timestamp, decision-engine version, and relevant provenance.
- Test all three outcomes and conflicting or insufficient evidence.
- Do not claim formal mathematical verification unless it has genuinely been implemented and demonstrated.

---

## 5. Technology Stack

### Frontend
- React.
- TypeScript.
- Vite.
- React Router.
- Reusable components.
- CSS or CSS modules using design tokens.
- A charting library only where useful.
- A consistent icon library or inline SVG icons.

### Backend
- Java.
- Spring Boot.
- Spring Security.
- REST APIs.
- Spring Data JPA.
- Bean Validation.
- Maven.

### Database
- MySQL.
- Flyway database migrations.
- Relational tables, constraints, foreign keys, indexes, and transactions.

### Testing
- JUnit 5.
- Spring Boot integration tests.
- Frontend unit tests.
- API tests.
- Playwright or an equivalent browser-testing tool when available.

Use compatible dependency versions. Do not create a second Node.js backend or use SQLite as a replacement for MySQL.

If a dependency or service is unavailable, report the actual limitation and continue with the supported work.

---

## 6. Project Structure

Create a maintainable structure similar to:

```text
EQEL/
├── EQEL_MASTER_PROMPT.md
├── frontend/
│   ├── public/
│   │   └── videos/
│   │       └── kingfisher-hero.mp4
│   ├── src/
│   │   ├── app/
│   │   ├── components/
│   │   ├── layouts/
│   │   ├── pages/
│   │   │   ├── public/
│   │   │   ├── auth/
│   │   │   ├── admin/
│   │   │   ├── placement/
│   │   │   └── student/
│   │   ├── features/
│   │   ├── services/
│   │   ├── hooks/
│   │   ├── types/
│   │   ├── styles/
│   │   └── tests/
│   └── package.json
├── backend/
│   ├── src/main/java/
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/
│   └── pom.xml
├── docs/
│   ├── architecture.md
│   ├── api.md
│   ├── database.md
│   └── testing.md
├── .env.example
├── .gitignore
└── README.md
```

Adapt the package structure where necessary while maintaining clear separation of responsibilities.

Never commit secrets, database passwords, or production credentials.

---

## 7. Global Design System

The entire application must use one coherent visual identity inspired by the KINGFISHER cinematic website.

Use these CSS variables:

```css
:root {
  --bg: #B6C3B0;
  --paper: #E3E8DE;
  --ink: #10201F;
  --ink-2: #2E3D3A;
  --ink-3: #4E5E58;
  --line: rgba(16, 32, 31, 0.14);
  --ghost: #ADBAA7;
  --orange: #E8732A;
  --orange-2: #F3A15E;
  --teal: #0E7C86;
  --panel: #0F1D1C;
  --panel-2: #172A28;

  --radius-card: 22px;
  --radius-panel: 26px;
  --radius-pill: 999px;
}
```

Typography:
- Instrument Serif for editorial headings.
- Manrope for body text, navigation, forms, and tables.
- JetBrains Mono for technical metadata.

Design principles:
- Sage-green and paper surfaces.
- Dark forest-green navigation and panels.
- Teal for verified evidence and informational states.
- Restrained orange for primary actions and important warnings.
- Rounded cards, subtle shadows, thin borders, and generous spacing.
- Consistent buttons, forms, tables, badges, and dialogs.
- Accessible contrast and visible keyboard focus.
- Subtle animations with reduced-motion support.

Avoid excessive gradients, purple accents, emoji-heavy UI, generic dashboard templates, and unnecessary glass effects.

Design every page for its specific purpose. Do not make every page look like the same arrangement of cards.

---

## 8. Cinematic Landing Page and Video Setup

Create a premium cinematic landing page featuring a kingfisher flying and landing against a sage-green background.

### Video source

Local video path:

`frontend/public/videos/kingfisher-hero.mp4`

Remote fallback:

`https://thinkingods.com/demos/kingfisher-hero/hero.mp4`

### Video workflow

1. Check whether the local video exists and is playable.
2. If it exists, use it as the primary source.
3. If it does not exist, use the remote URL directly.
4. Attempt to download the remote video only if downloading is available and permitted. Do not repeatedly retry failures.
5. Never create a fake local video file or claim an unsuccessful download worked.
6. Keep video paths easy to change.
7. If neither source works, display the fallback background and reveal all content immediately.

### Playback requirements

- Muted autoplay where supported.
- Inline playback.
- No looping.
- `object-fit: cover`.
- Sensible positioning and cropping.
- Sage-green fallback background.
- A final-frame hold when playback completes.
- Reduced-motion support.
- Appropriate event-listener and timeout cleanup.

Use a reveal trigger around 4.3 seconds.

Reveal the interface immediately if:
- Autoplay is rejected.
- Video playback fails.
- The video ends early.
- The video is already past the reveal time when handlers attach.
- Reduced-motion mode is active.
- A finite timeout expires.

The page must never leave essential content hidden while waiting for video playback.

### Hero composition

Large wordmark:

**EQEL**

Eyebrow:

`CAMPUS PLACEMENT · DECISION INTELLIGENCE`

Headline:

**Eligibility decisions you can explain.**

Supporting text:

“Evaluate student evidence against versioned placement policies, understand the reasoning behind every outcome, and identify cases requiring further verification.”

Primary CTA:

**LOGIN →**

The CTA must navigate to the actual role-selection page.

Create two restrained dark-green floating panels:

- **ELIGIBILITY ENGINE:** Illustrate evidence, mandatory rules, and an overall outcome.
- **EVIDENCE REVIEW:** Illustrate missing or conflicting evidence and the next resolution step.

Clearly label any illustrative data.

### Below-the-fold section

Title:

**From evidence to an explainable decision.**

Explain four stages:

1. Evidence.
2. Policy.
3. Decision.
4. Provenance.

Add a final CTA to the role-selection page.

Do not retain unrelated KINGFISHER library content.

---

## 9. Authentication and Role Selection

Implement three roles.

### Administrator
Manages users, roles, permissions, audit history, and supported system settings.

### Placement Officer
Manages students, companies, placement drives, eligibility policies, evidence reviews, evaluations, and decision history.

### Student
Views personal profile, evidence, eligibility results, placement drives, applications, and decision history.

Create a responsive role-selection page with three clear cards. Each card leads to the appropriate login experience.

Implement:
- Username or institutional email.
- Password with show/hide.
- Form validation.
- Loading states.
- Useful authentication errors.
- Back navigation.
- Secure password hashing.
- Server-side authentication and authorization.
- Appropriate session management.
- Logout and session invalidation.
- Protected frontend routes.
- Protected backend endpoints.
- Role mismatch rejection.
- Access-denied handling.
- Protection against repeated login attempts.
- CSRF protection where applicable.

Role selection is not authorization. Trust the authenticated user's actual server-side role.

Never implement fake login success or hardcode production credentials.

Provide a documented, development-only Administrator bootstrap mechanism. Do not expose public Administrator registration.

---

## 10. Administrator Workspace

Create a dark-green sidebar and a light workspace.

Navigation:
- Overview.
- User Management.
- Roles & Permissions.
- Audit Logs.
- Policy & Decision History.
- System Health.
- System Settings.
- My Profile.

Implement:
- User listing, search, filters, and pagination.
- User creation and permitted editing.
- Authorized role assignment.
- Account activation and deactivation.
- Secure password-reset initiation.
- Role and permission views.
- Audit-event browsing.
- Read-only policy and decision oversight by default.
- Real application and database health checks where available.
- Supported system settings.

Prevent accidental removal or deactivation of the last usable Administrator.

Record security-sensitive administrative changes in audit history.

Do not fabricate metrics or pretend unsupported monitoring and backup features exist.

---

## 11. Placement Officer Workspace

Navigation:
- Overview.
- Student Management.
- Companies & Drives.
- Eligibility Policies.
- Eligibility Evaluation.
- Evidence Review.
- Decision History.
- Applications & Reports.
- My Profile.

Implement:
- Searchable student directory.
- Student profiles and academic information.
- Evidence status.
- Company creation and editing.
- Placement-drive creation and editing.
- Deadlines and drive states.
- Policy association and validation.
- Versioned eligibility-policy management.
- Individual and authorized batch evaluation.
- Rule-level explanations.
- Evidence-resolution tasks.
- Decision history.
- Reports based on actual application records.

Preserve every policy version used in a recorded decision.

Create new policy versions instead of silently changing historical rules.

Students must not approve their own evidence.

Eligibility status and application status must remain separate.

---

## 12. Student Workspace

Navigation:
- Overview.
- My Profile.
- My Evidence.
- My Eligibility Results.
- Placement Drives.
- My Applications.
- Decision History.
- Account Settings.

Implement:
- Personal profile.
- Permitted profile edits.
- Protected academic-data correction requests.
- Evidence status and reviewer feedback.
- Secure evidence uploads if implemented.
- Open placement drives.
- Recorded eligibility results.
- Actual application statuses.
- Historical decisions and policy versions.
- Supported account settings.

Enforce student ownership checks on every relevant backend endpoint.

Students must not view another student's private records, approve their own evidence, change issued decisions, modify eligibility policies, or access privileged APIs.

Eligibility does not guarantee shortlisting or selection.

---

## 13. Database and Domain Model

Use normalized relational entities and Flyway migrations.

Include:
- Users and roles.
- Student profiles.
- Evidence records and revisions.
- Evidence verification history.
- Companies.
- Placement drives.
- Eligibility policies.
- Policy versions and rules.
- Evaluation records.
- Rule-level results.
- Evidence-resolution tasks.
- Applications.
- Audit events.
- Decision replay or change-impact records where implemented.

Use appropriate foreign keys, unique constraints, validation, indexes, and transaction boundaries.

Persist business data in MySQL.

Historical evaluations must reference the original policy version and evidence revisions or immutable snapshot. Preserve sufficient information to explain historical outcomes.

Do not store passwords in plaintext.

Do not store the entire application as one unstructured JSON object.

Create and test the database migrations.

---

## 14. Eligibility Engine

Implement the eligibility engine as an independent, testable backend module.

Each configured rule must produce an explainable result:
- Satisfied.
- Violated.
- Unverifiable.

The engine must apply the mandatory overall-outcome semantics to all admissible scenarios represented by the evidence model.

Requirements:
- Missing evidence is not automatically a violation.
- Conflicting evidence is handled explicitly.
- Rules come from the selected policy version.
- Rule results are reproducible from recorded inputs.
- Every overall outcome has an explanation.
- Historical decisions are not silently rewritten.
- New evaluations create new recorded decisions.

Write tests for:
1. All admissible scenarios satisfy all mandatory rules.
2. All admissible scenarios violate at least one mandatory rule.
3. Evidence is insufficient.
4. Evidence is conflicting.
5. Policy-version changes.
6. Historical decision preservation.

Do not rely exclusively on frontend calculations.

---

## 15. Evidence Provenance and Decision History

For every evaluation, preserve:
- Input evidence revisions or immutable snapshot.
- Policy version.
- Decision-engine version.
- Rule-level results.
- Overall outcome.
- Timestamp.
- Source and verification references where available.

Create a detailed decision view explaining how the outcome was reached.

Where replay is implemented:
- Identify affected records.
- Re-evaluate using the intended evidence and policy versions.
- Record replay parameters and results.
- Compare selective replay with full recomputation in automated tests.

Do not claim replay equivalence unless demonstrated by tests.

Never silently overwrite previous decisions.

---

## 16. Shared Components

Create reusable components for:
- EQEL branding.
- Sidebar and top navigation.
- Breadcrumbs and page headings.
- Buttons and form controls.
- Search and filters.
- Tables and pagination.
- Status badges.
- Eligibility result cards.
- Evidence indicators.
- Dialogs and confirmation prompts.
- Toast notifications.
- Loading skeletons.
- Empty states.
- Error states.
- Profile menus.

Use the shared design system throughout the application.

Every visible button must perform a meaningful action. Do not create fake save operations or decorative controls.

---

## 17. API Quality and Security

Use REST APIs with:
- Consistent resource naming.
- Request and response DTOs.
- Bean Validation.
- Centralized exception handling.
- Appropriate HTTP status codes.
- Pagination.
- Server-side ownership and permission checks.
- Transactions for multi-step operations.
- Safe logging that excludes secrets.

Never trust roles, user IDs, policy versions, or eligibility outcomes supplied by the frontend without server-side validation.

Use environment variables for credentials and secrets.

Provide `.env.example` with placeholders only.

Separate development and production configuration.

---

## 18. Responsive Design and Accessibility

Test at:
- 1440px.
- 1024px.
- 768px.
- 390px.
- 360px.

Requirements:
- No unintended horizontal overflow.
- Responsive navigation and tables.
- Mobile-friendly forms.
- Adequate touch targets.
- Accessible labels and keyboard navigation.
- Visible focus indicators.
- Sufficient contrast.
- Reduced-motion support.
- Clear loading, success, and error feedback.

The landing page can be cinematic. Authenticated pages must prioritize readability and speed.

---

## 19. Implementation Phases

### Phase 1 — Foundation
- Inspect the workspace and development environment.
- Initialize the frontend and backend.
- Configure MySQL and Flyway.
- Establish the design tokens and shared components.
- Create architecture documentation.
- Verify frontend and backend startup.

### Phase 2 — Security
- Implement authentication.
- Implement roles and permissions.
- Protect routes and APIs.
- Create the development-only Administrator bootstrap.
- Test authorization restrictions.

### Phase 3 — Public and Authentication UI
- Implement the cinematic landing page.
- Implement role selection.
- Implement role-specific login experiences.
- Test navigation and authentication.

### Phase 4 — Administrator
- Implement all Administrator pages.
- Connect pages to actual APIs and database records.
- Test user and role management.

### Phase 5 — Placement Officer
- Implement student, company, drive, policy, evaluation, evidence, history, and reporting workflows.
- Test persistence and permissions.

### Phase 6 — Eligibility and Evidence
- Implement the eligibility engine.
- Implement evidence revisions and verification.
- Implement historical references.
- Test all outcome semantics.

### Phase 7 — Student
- Implement profile, evidence, eligibility, drives, applications, and history.
- Test ownership restrictions.

### Phase 8 — Decision Ledger and Replay
- Complete historical explanations.
- Implement change-impact analysis and replay where feasible.
- Test reproducibility and replay equivalence where claimed.

### Phase 9 — Final Quality
- Run frontend tests and build.
- Run backend tests.
- Run migration tests.
- Test critical workflows.
- Verify responsive layouts.
- Fix observed defects.
- Complete documentation.

After each phase, report:
- Files created or changed.
- Features implemented.
- Tests actually executed and their results.
- Remaining blockers.

Continue to the next phase without waiting for approval unless a genuine external dependency requires user input.

Do not stop after planning or Phase 1.

---

## 20. Final Acceptance Criteria

The application is ready for delivery only when:

1. Frontend and backend start successfully.
2. Database migrations apply successfully.
3. Authentication and role enforcement work.
4. All three roles have functional workspaces.
5. Business operations persist in MySQL.
6. Eligibility outcomes follow the specified semantics.
7. Rule explanations match actual evaluation results.
8. Historical decisions preserve the versions used.
9. Students can access only their own records.
10. Sensitive actions are authorized and audited.
11. Responsive layouts are usable.
12. Automated tests and builds have been run.
13. Setup instructions are complete.
14. Remaining limitations are documented.

Do not claim an acceptance criterion has passed if it remains untested or incomplete.

---

## 21. Final Report

Provide:
- Architecture and folder structure.
- Implemented pages and modules.
- Database schema and migration status.
- Authentication and authorization details.
- API summary.
- Exact test commands and results.
- Build results.
- Startup instructions.
- Local URLs.
- Development Administrator setup instructions.
- Remaining limitations and known defects.

---

## FINAL COMMAND

Start with the current workspace.

If it is empty, create the complete EQEL application from zero using React + TypeScript + Vite, Spring Boot + Spring Security, and MySQL.

Read this specification completely before implementing. Work in phases, execute the code, test actual workflows, debug failures, and continue until the supported scope is complete.

**Build a real, secure, evidence-aware application. Do not deliver only a beautiful UI, a static prototype, fake login, mock persistence, or non-functional buttons.**