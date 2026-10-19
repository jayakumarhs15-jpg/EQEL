import { useState, type FormEvent } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../../app/AuthContext';
import type { Role } from '../../types';
import './AuthPages.css';

interface RoleConfig {
  role: Role;
  name: string;
  badge: string;
  subtitle: string;
  dashboardPath: string;
}

const ROLE_MAP: Record<string, RoleConfig> = {
  admin: {
    role: 'ADMIN',
    name: 'Administrator',
    badge: 'System Governance',
    subtitle: 'Sign in to access system control and audit records',
    dashboardPath: '/admin/overview',
  },
  placement: {
    role: 'PLACEMENT_OFFICER',
    name: 'Placement Officer',
    badge: 'Placement & Policy',
    subtitle: 'Sign in to manage policies, drives, and evaluations',
    dashboardPath: '/placement/overview',
  },
  student: {
    role: 'STUDENT',
    name: 'Student',
    badge: 'Applicant Portal',
    subtitle: 'Sign in with your enrollment details to check eligibility',
    dashboardPath: '/student/overview',
  },
};

export default function LoginPage() {
  const { roleKey } = useParams<{ roleKey: string }>();
  const navigate = useNavigate();
  const { login } = useAuth();

  const config = ROLE_MAP[roleKey?.toLowerCase() || 'student'] || ROLE_MAP.student;

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);

    if (!username.trim()) {
      setErrorMessage('Please enter your username or email');
      return;
    }
    if (!password) {
      setErrorMessage('Please enter your password');
      return;
    }

    setIsLoading(true);
    try {
      await login({
        username: username.trim(),
        password,
        role: config.role,
      });
      navigate(config.dashboardPath, { replace: true });
    } catch (err: unknown) {
      const errorObj = err as { message?: string };
      setErrorMessage(errorObj?.message || 'Authentication failed. Please verify credentials.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="auth-wrapper">
      <nav className="auth-nav">
        <Link to="/" className="landing-brand">
          <div className="landing-logo-badge">E</div>
          <span className="landing-logo-text">EQEL</span>
        </Link>
        <Link to="/roles" className="btn btn-ghost btn-sm">
          ← Switch Portal
        </Link>
      </nav>

      <main className="auth-container">
        <div className="login-box-container">
          <div className="login-card">
            <div className="login-header">
              <div className="login-role-pill">
                <span>{config.badge}</span>
              </div>
              <h1 className="login-title">{config.name} Login</h1>
              <p className="login-subtitle">{config.subtitle}</p>
            </div>

            {errorMessage && (
              <div className="login-error-alert" role="alert" style={{ marginBottom: 'var(--space-4)' }}>
                {errorMessage}
              </div>
            )}

            <form onSubmit={handleSubmit} className="login-form">
              <div className="form-group">
                <label className="form-label" htmlFor="username">
                  Username or Institutional Email
                </label>
                <input
                  id="username"
                  type="text"
                  className="form-input"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="e.g. admin or roll-number"
                  autoComplete="username"
                  required
                  disabled={isLoading}
                />
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="password">
                  Password
                </label>
                <div className="password-input-wrapper">
                  <input
                    id="password"
                    type={showPassword ? 'text' : 'password'}
                    className="form-input"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Enter password"
                    autoComplete="current-password"
                    required
                    disabled={isLoading}
                  />
                  <button
                    type="button"
                    className="password-toggle-btn"
                    onClick={() => setShowPassword(!showPassword)}
                    aria-label={showPassword ? 'Hide password' : 'Show password'}
                  >
                    {showPassword ? 'Hide' : 'Show'}
                  </button>
                </div>
              </div>

              <button
                type="submit"
                className="btn btn-primary"
                style={{ width: '100%', marginTop: 'var(--space-2)' }}
                disabled={isLoading}
              >
                {isLoading ? 'Verifying...' : `Sign in as ${config.name} →`}
              </button>
            </form>

            <div className="login-help-text">
              Protected by server-side role validation & BCrypt cryptographic hashing.
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
