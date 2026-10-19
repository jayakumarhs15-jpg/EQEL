import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../../services/api';
import type { PlacementDrive, Page } from '../../types';

export default function PlacementOverviewPage() {
  const [drives, setDrives] = useState<PlacementDrive[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get<Page<PlacementDrive>>('/api/placement/drives?size=5')
      .then(res => setDrives(res.content || []))
      .catch(() => setDrives([]))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div>
      <div className="breadcrumb">
        <span>Placement Office</span>
        <span className="breadcrumb-separator">/</span>
        <span>Overview</span>
      </div>

      <div className="page-header">
        <div>
          <h1 className="page-title">Placement Command Center</h1>
          <p className="page-subtitle">Configure versioned eligibility criteria, schedule recruitment drives, and evaluate candidate pools.</p>
        </div>
        <div style={{ display: 'flex', gap: 'var(--space-3)' }}>
          <Link to="/placement/drives" className="btn btn-primary btn-sm">
            + Schedule Drive
          </Link>
          <Link to="/placement/evaluate" className="btn btn-secondary btn-sm">
            ⚡ Evaluate Eligibility
          </Link>
        </div>
      </div>

      {/* ── Key Workflow Navigation Cards ── */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: 'var(--space-6)', marginBottom: 'var(--space-8)' }}>
        <div className="card">
          <div style={{ fontSize: '2rem', marginBottom: 'var(--space-2)' }}>📋</div>
          <h2 style={{ fontSize: '1.25rem', marginBottom: 'var(--space-1)' }}>Versioned Policies</h2>
          <p style={{ fontSize: '0.875rem', color: 'var(--ink-3)', marginBottom: 'var(--space-4)' }}>
            Author criteria (CGPA, backlogs, department) with version immutability.
          </p>
          <Link to="/placement/policies" className="btn btn-outline btn-sm">
            Manage Policies →
          </Link>
        </div>

        <div className="card">
          <div style={{ fontSize: '2rem', marginBottom: 'var(--space-2)' }}>⚡</div>
          <h2 style={{ fontSize: '1.25rem', marginBottom: 'var(--space-1)' }}>Run Eligibility Engine</h2>
          <p style={{ fontSize: '0.875rem', color: 'var(--ink-3)', marginBottom: 'var(--space-4)' }}>
            Execute deterministic rule evaluation for candidates against locked drives.
          </p>
          <Link to="/placement/evaluate" className="btn btn-outline btn-sm">
            Launch Engine →
          </Link>
        </div>

        <div className="card">
          <div style={{ fontSize: '2rem', marginBottom: 'var(--space-2)' }}>📑</div>
          <h2 style={{ fontSize: '1.25rem', marginBottom: 'var(--space-1)' }}>Evidence Verification</h2>
          <p style={{ fontSize: '0.875rem', color: 'var(--ink-3)', marginBottom: 'var(--space-4)' }}>
            Review pending student marks, degree certificates, and resolve conflicts.
          </p>
          <Link to="/placement/evidence" className="btn btn-outline btn-sm">
            Review Queue →
          </Link>
        </div>

        <div className="card">
          <div style={{ fontSize: '2rem', marginBottom: 'var(--space-2)' }}>⚖️</div>
          <h2 style={{ fontSize: '1.25rem', marginBottom: 'var(--space-1)' }}>Decision History</h2>
          <p style={{ fontSize: '0.875rem', color: 'var(--ink-3)', marginBottom: 'var(--space-4)' }}>
            Trace past eligibility outcomes to their original evidence and policy snapshots.
          </p>
          <Link to="/placement/history" className="btn btn-outline btn-sm">
            Browse Ledger →
          </Link>
        </div>
      </div>

      {/* ── Active Hiring Drives ── */}
      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <div style={{ padding: 'var(--space-4) var(--space-6)', borderBottom: '1px solid var(--line)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h3 style={{ fontSize: '1.125rem' }}>Active & Upcoming Placement Drives</h3>
          <Link to="/placement/drives" style={{ fontSize: '0.8125rem', fontWeight: 600 }}>
            View All Drives →
          </Link>
        </div>

        <table className="eqel-table">
          <thead>
            <tr>
              <th>Drive Details</th>
              <th>Package (CTC)</th>
              <th>Status</th>
              <th>Drive Date</th>
              <th style={{ textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={5} style={{ textAlign: 'center', padding: 'var(--space-6)' }}>
                  Loading drives...
                </td>
              </tr>
            ) : drives.length === 0 ? (
              <tr>
                <td colSpan={5} style={{ textAlign: 'center', padding: 'var(--space-6)', color: 'var(--ink-3)' }}>
                  No placement drives scheduled yet. Click "+ Schedule Drive" above.
                </td>
              </tr>
            ) : (
              drives.map(d => (
                <tr key={d.id}>
                  <td>
                    <div style={{ fontWeight: 600, color: 'var(--ink)' }}>{d.title}</div>
                    <div style={{ fontSize: '0.8125rem', color: 'var(--ink-3)' }}>
                      {d.companyName || 'Corporate Recruiter'} · {d.jobRole || 'Software Engineer'}
                    </div>
                  </td>
                  <td style={{ fontFamily: 'var(--font-mono)', fontWeight: 600 }}>
                    ₹{d.packageLpa ? `${d.packageLpa} LPA` : 'Competitive'}
                  </td>
                  <td>
                    <span className="badge badge-eligible">{d.driveStatus}</span>
                  </td>
                  <td style={{ fontSize: '0.8125rem', fontFamily: 'var(--font-mono)' }}>
                    {d.driveDate ? new Date(d.driveDate).toLocaleDateString() : 'TBD'}
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    <Link to="/placement/evaluate" className="btn btn-outline btn-sm" style={{ fontSize: '0.75rem' }}>
                      Evaluate
                    </Link>
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
