import { Link } from 'react-router-dom';
import './AuthPages.css';

export default function RoleSelectPage() {
  return (
    <div className="auth-wrapper">
      <nav className="auth-nav">
        <Link to="/" className="landing-brand">
          <div className="landing-logo-badge">E</div>
          <span className="landing-logo-text">EQEL</span>
        </Link>
        <Link to="/" className="btn btn-ghost btn-sm">
          ← Back to Overview
        </Link>
      </nav>

      <main className="auth-container">
        <div className="role-select-header">
          <h1 className="role-select-title">Select Portal</h1>
          <p className="role-select-subtitle">
            EQEL provides tailored workflows based on your institutional responsibility.
          </p>
        </div>

        <div className="role-cards-grid">
          {/* Card 1: Administrator */}
          <Link to="/login/admin" className="role-card admin">
            <div>
              <div className="role-card-badge">Governance</div>
              <div className="role-card-icon">⚙️</div>
              <h2 className="role-card-title">Administrator</h2>
              <p className="role-card-desc">
                Oversee system users, assign institutional roles, inspect security audit trails, and maintain global settings.
              </p>
            </div>
            <div className="role-card-action">
              Access Admin Portal →
            </div>
          </Link>

          {/* Card 2: Placement Officer */}
          <Link to="/login/placement" className="role-card placement">
            <div>
              <div className="role-card-badge">Evaluation & Drives</div>
              <div className="role-card-icon">📊</div>
              <h2 className="role-card-title">Placement Officer</h2>
              <p className="role-card-desc">
                Configure versioned eligibility policies, schedule hiring drives, review student academic evidence, and trigger evaluations.
              </p>
            </div>
            <div className="role-card-action">
              Access Placement Portal →
            </div>
          </Link>

          {/* Card 3: Student */}
          <Link to="/login/student" className="role-card student">
            <div>
              <div className="role-card-badge">Applicant</div>
              <div className="role-card-icon">🎓</div>
              <h2 className="role-card-title">Student</h2>
              <p className="role-card-desc">
                Submit evidence, verify personal academic records, check drive qualification status, and apply for open company positions.
              </p>
            </div>
            <div className="role-card-action">
              Access Student Portal →
            </div>
          </Link>
        </div>
      </main>
    </div>
  );
}
