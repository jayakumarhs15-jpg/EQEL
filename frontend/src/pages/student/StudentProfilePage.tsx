import { useState, useEffect, type FormEvent } from 'react';
import api from '../../services/api';
import type { StudentProfile } from '../../types';

export default function StudentProfilePage() {
  const [profile, setProfile] = useState<StudentProfile | null>(null);
  const [phone, setPhone] = useState('');
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    api.get<StudentProfile>('/api/student/profile')
      .then(p => {
        setProfile(p);
        setPhone(p.phone || '');
      })
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const handleUpdate = async (e: FormEvent) => {
    e.preventDefault();
    try {
      const updated = await api.put<StudentProfile>('/api/student/profile', { phone });
      setProfile(updated);
      setMessage('Contact information updated successfully.');
      setTimeout(() => setMessage(null), 3000);
    } catch {
      alert('Failed to update profile.');
    }
  };

  return (
    <div>
      <div className="breadcrumb">
        <span>Student Portal</span>
        <span className="breadcrumb-separator">/</span>
        <span>My Profile</span>
      </div>

      <div className="page-header">
        <div>
          <h1 className="page-title">Candidate Profile & Academic Ledger</h1>
          <p className="page-subtitle">Your locked institutional academic record. To contest grades or backlogs, submit evidence to the Placement Office.</p>
        </div>
      </div>

      {message && (
        <div className="card" style={{ background: 'rgba(45, 140, 78, 0.12)', borderLeft: '3px solid var(--success)', marginBottom: 'var(--space-6)', padding: 'var(--space-4)' }}>
          {message}
        </div>
      )}

      {loading ? (
        <div className="card" style={{ padding: 'var(--space-8)', textAlign: 'center' }}>
          Loading candidate record...
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: '1.2fr 0.8fr', gap: 'var(--space-8)' }}>
        {/* ── Locked Academic Data ── */}
        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 'var(--space-4)', borderBottom: '1px solid var(--line)', paddingBottom: 'var(--space-3)' }}>
            <h2 style={{ fontSize: '1.25rem' }}>Academic Baseline Record</h2>
            <span className="badge badge-verified">Registrar Verified</span>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-4)', fontSize: '0.875rem' }}>
            <div>
              <span style={{ color: 'var(--ink-3)', fontSize: '0.75rem', textTransform: 'uppercase', fontFamily: 'var(--font-mono)' }}>Enrollment ID</span>
              <div style={{ fontWeight: 700, fontSize: '1.125rem', marginTop: 'var(--space-1)' }}>
                {profile?.enrollmentNumber || 'ENR-2026-001'}
              </div>
            </div>

            <div>
              <span style={{ color: 'var(--ink-3)', fontSize: '0.75rem', textTransform: 'uppercase', fontFamily: 'var(--font-mono)' }}>Department</span>
              <div style={{ fontWeight: 600, marginTop: 'var(--space-1)' }}>
                {profile?.department || 'Computer Science & Engineering'}
              </div>
            </div>

            <div>
              <span style={{ color: 'var(--ink-3)', fontSize: '0.75rem', textTransform: 'uppercase', fontFamily: 'var(--font-mono)' }}>Program</span>
              <div style={{ fontWeight: 600, marginTop: 'var(--space-1)' }}>
                {profile?.program || 'Bachelor of Technology'}
              </div>
            </div>

            <div>
              <span style={{ color: 'var(--ink-3)', fontSize: '0.75rem', textTransform: 'uppercase', fontFamily: 'var(--font-mono)' }}>Semester</span>
              <div style={{ fontWeight: 600, marginTop: 'var(--space-1)' }}>
                Semester {profile?.semester || '7'}
              </div>
            </div>

            <div>
              <span style={{ color: 'var(--ink-3)', fontSize: '0.75rem', textTransform: 'uppercase', fontFamily: 'var(--font-mono)' }}>Cumulative GPA</span>
              <div style={{ fontWeight: 700, fontSize: '1.25rem', color: 'var(--teal)', marginTop: 'var(--space-1)' }}>
                {profile?.cgpa ? Number(profile.cgpa).toFixed(2) : '8.45'}
              </div>
            </div>

            <div>
              <span style={{ color: 'var(--ink-3)', fontSize: '0.75rem', textTransform: 'uppercase', fontFamily: 'var(--font-mono)' }}>Active Backlogs</span>
              <div style={{ fontWeight: 700, fontSize: '1.25rem', color: profile?.backlogs === 0 ? 'var(--success)' : 'var(--danger)', marginTop: 'var(--space-1)' }}>
                {profile?.backlogs ?? 0}
              </div>
            </div>
          </div>
        </div>

        {/* ── Contact Details Edit ── */}
        <div className="card">
          <h2 style={{ fontSize: '1.25rem', marginBottom: 'var(--space-4)' }}>Contact Information</h2>
          <form onSubmit={handleUpdate} className="login-form">
            <div className="form-group">
              <label className="form-label">Full Name (Locked)</label>
              <input
                type="text"
                className="form-input"
                disabled
                value={profile?.fullName || ''}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Institutional Email (Locked)</label>
              <input
                type="email"
                className="form-input"
                disabled
                value={profile?.email || ''}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Contact Phone Number</label>
              <input
                type="tel"
                className="form-input"
                value={phone}
                onChange={e => setPhone(e.target.value)}
                placeholder="+91 98765 43210"
              />
            </div>

            <button type="submit" className="btn btn-outline" style={{ marginTop: 'var(--space-3)' }}>
              Save Contact Details
            </button>
          </form>
        </div>
      </div>
      )}
    </div>
  );
}
