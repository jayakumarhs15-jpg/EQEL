# EQEL — API Documentation

All API endpoints reside under `/api` and require appropriate session cookies.

## Authentication Endpoints (`/api/auth`)
| Method | Endpoint | Description | Role Required |
|---|---|---|---|
| `POST` | `/api/auth/login` | Authenticate with `{username, password, role}` | Public |
| `POST` | `/api/auth/logout` | Terminate session and invalidate cookie | Authenticated |
| `GET` | `/api/auth/me` | Fetch currently authenticated user identity | Authenticated |

## Administration Endpoints (`/api/admin`)
| Method | Endpoint | Description | Role Required |
|---|---|---|---|
| `GET` | `/api/admin/users` | List users with search, role filter, pagination | `ADMIN` |
| `POST` | `/api/admin/users` | Create a new user (with role assignment) | `ADMIN` |
| `PUT` | `/api/admin/users/{id}/status` | Toggle user active/inactive state | `ADMIN` |
| `POST` | `/api/admin/users/{id}/reset-password` | Set new password for user | `ADMIN` |
| `GET` | `/api/admin/audit-logs` | Retrieve system audit logs with filters | `ADMIN` |
| `GET` | `/api/admin/system-health` | Fetch backend and database health status | `ADMIN` |

## Placement Officer Endpoints (`/api/placement`)
| Method | Endpoint | Description | Role Required |
|---|---|---|---|
| `GET` | `/api/placement/students` | Search and paginate student profiles | `PLACEMENT_OFFICER` |
| `GET` | `/api/placement/companies` | List all registered companies | `PLACEMENT_OFFICER` |
| `POST` | `/api/placement/companies` | Create a new company profile | `PLACEMENT_OFFICER` |
| `GET` | `/api/placement/drives` | List all placement drives | `PLACEMENT_OFFICER` |
| `POST` | `/api/placement/drives` | Create a new placement drive | `PLACEMENT_OFFICER` |
| `PUT` | `/api/placement/drives/{id}` | Update placement drive details/status | `PLACEMENT_OFFICER` |
| `GET` | `/api/placement/policies` | List all eligibility policies & versions | `PLACEMENT_OFFICER` |
| `POST` | `/api/placement/policies` | Create a new policy or version | `PLACEMENT_OFFICER` |
| `POST` | `/api/placement/evaluations/evaluate` | Trigger evaluation for student & drive | `PLACEMENT_OFFICER` |
| `GET` | `/api/placement/evidence/pending` | List pending student evidence | `PLACEMENT_OFFICER` |
| `PUT` | `/api/placement/evidence/{id}/verify` | Approve or reject evidence record | `PLACEMENT_OFFICER` |
| `GET` | `/api/placement/evaluations/history` | Browse decision ledger with filters | `PLACEMENT_OFFICER` |

## Student Endpoints (`/api/student`)
| Method | Endpoint | Description | Role Required |
|---|---|---|---|
| `GET` | `/api/student/profile` | View personal student profile | `STUDENT` |
| `PUT` | `/api/student/profile` | Update contact info (phone/address) | `STUDENT` |
| `GET` | `/api/student/evidence` | View personal submitted evidence | `STUDENT` |
| `POST` | `/api/student/evidence` | Submit new evidence record for review | `STUDENT` |
| `GET` | `/api/student/drives` | Browse active and upcoming drives | `STUDENT` |
| `GET` | `/api/student/eligibility` | View personal eligibility verdicts | `STUDENT` |
| `GET` | `/api/student/applications` | View personal drive applications | `STUDENT` |
| `POST` | `/api/student/applications/{driveId}` | Apply for eligible placement drive | `STUDENT` |
