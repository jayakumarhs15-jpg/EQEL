import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../../services/api';
import type { StudentProfile, EvaluationRecord, PlacementDrive } from '../../types';

export default function StudentOverviewPage() {
  const [profile, setProfile] = useState<StudentProfile | null>(null);
  const [evaluations, setEvaluations] = useState<EvaluationRecord[]>([]);
  const [drives, setDrives] = useState<PlacementDrive[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      api.get<StudentProfile>('/api/student/profile').catch(() => null),
      api.get<EvaluationRecord[]>('/api/student/eligibility').catch(() => []),
      api.get<PlacementDrive[]>('/api/student/drives').catch(() => []),
    ]).then(([p, e, d]) => {
      setProfile(p);
      setEvaluations(e || []);
      setDrives(d || []);
    }).finally(() => setLoading(false));
  }, []);

  return (
    <div>
      <div className="breadcrumb">
        <span>Student Portal</span>
        <span className="breadcrumb-separator">/</span>
        <span>Overview</span>
      </div>

      <div className="page-header">
        <div>
          <h1 className="page-title">Welcome, {profile?.fullName || 'Candidate'}</h1>
          <p className="page-subtitle">Track your academic credentials, review company eligibility, and submit job applications.</p>
        </div>
        <div style={{ display: 'flex', gap: 'var(--space-3)' }}>
          <Link to="/student/evidence" className="btn btn-primary btn-sm">
            + Submit Evidence
          </Link>
          <Link to="/student/drives" className="btn btn-outline btn-sm">
            Browse Drives
          </Link>
        </div>
      </div>

      {/* ── Summary Stats ── */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: 'var(--space-6)', marginBottom: 'var(--space-8)' }}>
        <div className="card">
          <div style={{ fontFamily: 'var(--font-mono)', fontSize: '0.75rem', color: 'var(--ink-3)', textTransform: 'uppercase' }}>
            Current CGPA
          </div>
          <div style={{ fontFamily: 'var(--font-display)', fontSize: '2rem', color: 'var(--teal)', marginTop: 'var(--space-1)' }}>
            {profile?.cgpa ? Number(profile.cgpa).toFixed(2) : 'N/A'}
          </div>
          <p style={{ fontSize: '0.8125rem', color: 'var(--ink-3)' }}>Verified academic marker</p>
        </div>

        <div className="card">
          <div style={{ fontFamily: 'var(--font-mono)', fontSize: '0.75rem', color: 'var(--ink-3)', textTransform: 'uppercase' }}>
            Active Backlogs
          </div>
          <div style={{ fontFamily: 'var(--font-display)', fontSize: '2rem', color: profile?.backlogs === 0 ? 'var(--success)' : 'var(--danger)', marginTop: 'var(--space-1)' }}>
            {profile?.backlogs ?? 0}
          </div>
          <p style={{ fontSize: '0.8125rem', color: 'var(--ink-3)' }}>{profile?.backlogs === 0 ? 'Clear academic record' : 'Requires remediation'}</p>
        </div>

        <div className="card">
          <div style={{ fontFamily: 'var(--font-mono)', fontSize: '0.75rem', color: 'var(--ink-3)', textTransform: 'uppercase' }}>
            Open Hiring Drives
          </div>
          <div style={{ fontFamily: 'var(--font-display)', fontSize: '2rem', color: 'var(--ink)', marginTop: 'var(--space-1)' }}>
            {drives.length}
          </div>
          <p style={{ fontSize: '0.8125rem', color: 'var(--ink-3)' }}>Active registration windows</p>
        </div>

        <div className="card">
          <div style={{ fontFamily: 'var(--font-mono)', fontSize: '0.75rem', color: 'var(--ink-3)', textTransform: 'uppercase' }}>
            Evaluations Recorded
          </div>
          <div style={{ fontFamily: 'var(--font-display)', fontSize: '2rem', color: 'var(--ink)', marginTop: 'var(--space-1)' }}>
            {evaluations.length}
          </div>
          <p style={{ fontSize: '0.8125rem', color: 'var(--ink-3)' }}>Recorded in ledger</p>
        </div>
      </div>

      {/* ── Recent Eligibility Outcomes ── */}
      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <div style={{ padding: 'var(--space-4) var(--space-6)', borderBottom: '1px solid var(--line)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h3 style={{ fontSize: '1.125rem' }}>Your Placement Drive Eligibility Results</h3>
          <Link to="/student/eligibility" style={{ fontSize: '0.8125rem', fontWeight: 600 }}>
            View Detailed Explanations →
          </Link>
        </div>

        <table className="eqel-table">
          <thead>
            <tr>
              <th>Evaluation ID</th>
              <th>Overall Outcome</th>
              <th>Engine Explanation</th>
              <th>Evaluated Date</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} style={{ textAlign: 'center', padding: 'var(--space-6)' }}>
                  Loading eligibility records...
                </td>
              </tr>
            ) : evaluations.length === 0 ? (
              <tr>
                <td colSpan={4} style={{ textAlign: 'center', padding: 'var(--space-6)', color: 'var(--ink-3)' }}>
                  No eligibility decisions have been recorded for your profile yet.
                </td>
              </tr>
            ) : (
              evaluations.map(ev => (
                <tr key={ev.id}>
                  <td style={{ fontFamily: 'var(--font-mono)', fontWeight: 600 }}>#{ev.id}</td>
                  <td>
                    <span className={`badge ${
                      ev.overallOutcome === 'ELIGIBLE' ? 'badge-eligible' :
                      ev.overallOutcome === 'INELIGIBLE' ? 'badge-ineligible' : 'badge-unverifiable'
                    }`}>
                      {ev.overallOutcome}
                    </span>
                  </td>
                  <td style={{ fontSize: '0.875rem' }}>{ev.explanation}</td>
                  <td style={{ fontSize: '0.8125rem', fontFamily: 'var(--font-mono)' }}>
                    {new Date(ev.evaluatedAt).toLocaleDateString()}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
