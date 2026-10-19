import { useState, type ReactNode } from 'react';
import { NavLink, useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../app/AuthContext';
import './DashboardLayout.css';

interface NavItem {
  label: string;
  path: string;
  icon: string;
}

const ADMIN_NAV: NavItem[] = [
  { label: 'Overview', path: '/admin/overview', icon: '📈' },
  { label: 'User Management', path: '/admin/users', icon: '👥' },
  { label: 'Roles & Permissions', path: '/admin/roles', icon: '🛡️' },
  { label: 'Audit Logs', path: '/admin/audit', icon: '📜' },
  { label: 'Decision History', path: '/admin/history', icon: '⚖️' },
  { label: 'System Health', path: '/admin/health', icon: '🩺' },
  { label: 'System Settings', path: '/admin/settings', icon: '⚙️' },
  { label: 'My Profile', path: '/admin/profile', icon: '👤' },
];

const PLACEMENT_NAV: NavItem[] = [
  { label: 'Overview', path: '/placement/overview', icon: '📊' },
  { label: 'Student Directory', path: '/placement/students', icon: '🎓' },
  { label: 'Companies & Drives', path: '/placement/drives', icon: '🏢' },
  { label: 'Eligibility Policies', path: '/placement/policies', icon: '📋' },
  { label: 'Evaluate Eligibility', path: '/placement/evaluate', icon: '⚡' },
  { label: 'Evidence Review', path: '/placement/evidence', icon: '📑' },
  { label: 'Decision Ledger', path: '/placement/history', icon: '⚖️' },
  { label: 'Applications & Reports', path: '/placement/reports', icon: '📑' },
  { label: 'My Profile', path: '/placement/profile', icon: '👤' },
];

const STUDENT_NAV: NavItem[] = [
  { label: 'Overview', path: '/student/overview', icon: '🏠' },
  { label: 'My Profile', path: '/student/profile', icon: '👤' },
  { label: 'My Evidence', path: '/student/evidence', icon: '📑' },
  { label: 'Eligibility Verdicts', path: '/student/eligibility', icon: '⚖️' },
  { label: 'Placement Drives', path: '/student/drives', icon: '🚀' },
  { label: 'My Applications', path: '/student/applications', icon: '📝' },
  { label: 'Decision History', path: '/student/history', icon: '📜' },
  { label: 'Account Settings', path: '/student/settings', icon: '⚙️' },
];

export default function DashboardLayout({ children }: { children: ReactNode }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [mobileOpen, setMobileOpen] = useState(false);

  const getNavItems = (): NavItem[] => {
    if (!user) return [];
    switch (user.role) {
      case 'ADMIN': return ADMIN_NAV;
      case 'PLACEMENT_OFFICER': return PLACEMENT_NAV;
      case 'STUDENT': return STUDENT_NAV;
      default: return [];
    }
  };

  const navItems = getNavItems();

  const handleLogout = async () => {
    await logout();
    navigate('/roles');
  };

  return (
    <div className="dashboard-container">
      {/* ── Sidebar ── */}
      <aside className={`sidebar ${mobileOpen ? 'open' : ''}`}>
        <div>
          <Link to="/" className="sidebar-brand">
            <div className="sidebar-logo-badge">E</div>
            <div>
              <div className="sidebar-brand-name">EQEL</div>
              <span className="sidebar-role-badge">{user?.role?.replace('_', ' ') || 'PORTAL'}</span>
            </div>
          </Link>

          <nav className="sidebar-nav">
            {navItems.map((item) => (
              <NavLink
                key={item.path}
                to={item.path}
                className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
                onClick={() => setMobileOpen(false)}
              >
                <span className="nav-item-icon">{item.icon}</span>
                <span>{item.label}</span>
              </NavLink>
            ))}
          </nav>
        </div>

        {/* ── Sidebar Footer / User Info ── */}
        <div className="sidebar-footer">
          <div className="user-snippet">
            <div className="user-avatar-circle">
              {user?.fullName?.charAt(0) || user?.username?.charAt(0) || 'U'}
            </div>
            <div className="user-meta">
              <span className="user-name">{user?.fullName || user?.username}</span>
              <span className="user-role-label">{user?.role}</span>
            </div>
          </div>
          <button
            type="button"
            className="logout-icon-btn"
            onClick={handleLogout}
            title="Sign out of EQEL"
            aria-label="Sign out"
          >
            🚪
          </button>
        </div>
      </aside>

      {/* ── Main Canvas ── */}
      <div className="main-wrapper">
        <header className="topbar">
          <div className="topbar-left">
            <button
              type="button"
              className="mobile-menu-toggle"
              onClick={() => setMobileOpen(!mobileOpen)}
              aria-label="Toggle navigation drawer"
            >
              ☰
            </button>
            <span style={{ fontFamily: 'var(--font-mono)', fontSize: '0.75rem', color: 'var(--ink-3)' }}>
              DECISION ENGINE v1.0.0 · EVIDENCE VERIFIED
            </span>
          </div>

          <div className="topbar-right">
            <Link to="/" className="btn btn-ghost btn-sm" target="_blank" rel="noreferrer">
              Public Site ↗
            </Link>
            <button
              type="button"
              onClick={handleLogout}
              className="btn btn-outline btn-sm"
            >
              Logout
            </button>
          </div>
        </header>

        <main className="workspace-content">
          {children}
        </main>
      </div>
    </div>
  );
}
