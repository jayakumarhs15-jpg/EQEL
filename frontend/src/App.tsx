import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './app/AuthContext';
import LandingPage from './pages/public/LandingPage';
import RoleSelectPage from './pages/auth/RoleSelectPage';
import LoginPage from './pages/auth/LoginPage';
import DashboardLayout from './layouts/DashboardLayout';

// Admin views
import AdminOverviewPage from './pages/admin/AdminOverviewPage';
import AdminUsersPage from './pages/admin/AdminUsersPage';
import AdminAuditPage from './pages/admin/AdminAuditPage';
import AdminHealthPage from './pages/admin/AdminHealthPage';

// Placement views
import PlacementOverviewPage from './pages/placement/PlacementOverviewPage';
import PlacementStudentsPage from './pages/placement/PlacementStudentsPage';
import PlacementDrivesPage from './pages/placement/PlacementDrivesPage';
import PlacementPoliciesPage from './pages/placement/PlacementPoliciesPage';
import PlacementEvaluatePage from './pages/placement/PlacementEvaluatePage';
import PlacementEvidencePage from './pages/placement/PlacementEvidencePage';
import PlacementHistoryPage from './pages/placement/PlacementHistoryPage';

// Student views
import StudentOverviewPage from './pages/student/StudentOverviewPage';
import StudentProfilePage from './pages/student/StudentProfilePage';
import StudentEvidencePage from './pages/student/StudentEvidencePage';
import StudentEligibilityPage from './pages/student/StudentEligibilityPage';
import StudentDrivesPage from './pages/student/StudentDrivesPage';

import type { Role } from './types';
import type { ReactNode } from 'react';

function ProtectedRoute({ allowedRole, children }: { allowedRole?: Role; children: ReactNode }) {
  const { user, isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div style={{ display: 'flex', minHeight: '100vh', alignItems: 'center', justifyContent: 'center', background: 'var(--bg)' }}>
        <div style={{ fontFamily: 'var(--font-mono)', fontSize: '0.875rem' }}>Loading session...</div>
      </div>
    );
  }

  if (!isAuthenticated || !user) {
    return <Navigate to="/roles" replace />;
  }

  if (allowedRole && user.role !== allowedRole) {
    return <Navigate to="/roles" replace />;
  }

  return <DashboardLayout>{children}</DashboardLayout>;
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<LandingPage />} />
          <Route path="/roles" element={<RoleSelectPage />} />
          <Route path="/login/:roleKey" element={<LoginPage />} />

          {/* Admin Protected Routes */}
          <Route path="/admin/overview" element={<ProtectedRoute allowedRole="ADMIN"><AdminOverviewPage /></ProtectedRoute>} />
          <Route path="/admin/users" element={<ProtectedRoute allowedRole="ADMIN"><AdminUsersPage /></ProtectedRoute>} />
          <Route path="/admin/roles" element={<ProtectedRoute allowedRole="ADMIN"><AdminUsersPage /></ProtectedRoute>} />
          <Route path="/admin/audit" element={<ProtectedRoute allowedRole="ADMIN"><AdminAuditPage /></ProtectedRoute>} />
          <Route path="/admin/history" element={<ProtectedRoute allowedRole="ADMIN"><PlacementHistoryPage /></ProtectedRoute>} />
          <Route path="/admin/health" element={<ProtectedRoute allowedRole="ADMIN"><AdminHealthPage /></ProtectedRoute>} />
          <Route path="/admin/settings" element={<ProtectedRoute allowedRole="ADMIN"><AdminHealthPage /></ProtectedRoute>} />
          <Route path="/admin/profile" element={<ProtectedRoute allowedRole="ADMIN"><AdminOverviewPage /></ProtectedRoute>} />

          {/* Placement Officer Protected Routes */}
          <Route path="/placement/overview" element={<ProtectedRoute allowedRole="PLACEMENT_OFFICER"><PlacementOverviewPage /></ProtectedRoute>} />
          <Route path="/placement/students" element={<ProtectedRoute allowedRole="PLACEMENT_OFFICER"><PlacementStudentsPage /></ProtectedRoute>} />
          <Route path="/placement/drives" element={<ProtectedRoute allowedRole="PLACEMENT_OFFICER"><PlacementDrivesPage /></ProtectedRoute>} />
          <Route path="/placement/policies" element={<ProtectedRoute allowedRole="PLACEMENT_OFFICER"><PlacementPoliciesPage /></ProtectedRoute>} />
          <Route path="/placement/evaluate" element={<ProtectedRoute allowedRole="PLACEMENT_OFFICER"><PlacementEvaluatePage /></ProtectedRoute>} />
          <Route path="/placement/evidence" element={<ProtectedRoute allowedRole="PLACEMENT_OFFICER"><PlacementEvidencePage /></ProtectedRoute>} />
          <Route path="/placement/history" element={<ProtectedRoute allowedRole="PLACEMENT_OFFICER"><PlacementHistoryPage /></ProtectedRoute>} />
          <Route path="/placement/reports" element={<ProtectedRoute allowedRole="PLACEMENT_OFFICER"><PlacementHistoryPage /></ProtectedRoute>} />
          <Route path="/placement/profile" element={<ProtectedRoute allowedRole="PLACEMENT_OFFICER"><PlacementOverviewPage /></ProtectedRoute>} />

          {/* Student Protected Routes */}
          <Route path="/student/overview" element={<ProtectedRoute allowedRole="STUDENT"><StudentOverviewPage /></ProtectedRoute>} />
          <Route path="/student/profile" element={<ProtectedRoute allowedRole="STUDENT"><StudentProfilePage /></ProtectedRoute>} />
          <Route path="/student/evidence" element={<ProtectedRoute allowedRole="STUDENT"><StudentEvidencePage /></ProtectedRoute>} />
          <Route path="/student/eligibility" element={<ProtectedRoute allowedRole="STUDENT"><StudentEligibilityPage /></ProtectedRoute>} />
          <Route path="/student/drives" element={<ProtectedRoute allowedRole="STUDENT"><StudentDrivesPage /></ProtectedRoute>} />
          <Route path="/student/applications" element={<ProtectedRoute allowedRole="STUDENT"><StudentDrivesPage /></ProtectedRoute>} />
          <Route path="/student/history" element={<ProtectedRoute allowedRole="STUDENT"><StudentEligibilityPage /></ProtectedRoute>} />
          <Route path="/student/settings" element={<ProtectedRoute allowedRole="STUDENT"><StudentProfilePage /></ProtectedRoute>} />

          {/* Fallback */}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
