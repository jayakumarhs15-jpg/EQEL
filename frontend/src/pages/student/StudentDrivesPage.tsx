import { useState, useEffect } from 'react';
import api from '../../services/api';
import type { PlacementDrive, Application } from '../../types';

export default function StudentDrivesPage() {
  const [drives, setDrives] = useState<PlacementDrive[]>([]);
  const [myApplications, setMyApplications] = useState<Application[]>([]);
  const [loading, setLoading] = useState(true);
  const [statusMsg, setStatusMsg] = useState<{ text: string; error?: boolean } | null>(null);

  const fetchData = () => {
    setLoading(true);
    Promise.all([
      api.get<PlacementDrive[]>('/api/student/drives').catch(() => []),
      api.get<Application[]>('/api/student/applications').catch(() => []),
    ]).then(([d, a]) => {
      setDrives(d || []);
      setMyApplications(a || []);
    }).finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleApply = async (driveId: number) => {
    setStatusMsg(null);
    try {
      await api.post(`/api/student/applications/${driveId}`);
      setStatusMsg({ text: 'Application submitted successfully!' });
      fetchData();
    } catch (err: unknown) {
      const errorObj = err as { message?: string };
      setStatusMsg({
        text: errorObj?.message || 'Could not apply. Ensure you have an ELIGIBLE evaluation verdict.',
        error: true,
      });
    }
  };

  const isApplied = (driveId: number) => {
    return myApplications.some(a => a.driveId === driveId);
  };

  return (
    <div>
      <div className="breadcrumb">
        <span>Student Portal</span>
        <span className="breadcrumb-separator">/</span>
        <span>Placement Drives</span>
      </div>

      <div className="page-header">
        <div>
          <h1 className="page-title">Open Placement Drives</h1>
          <p className="page-subtitle">Corporate hiring drives currently accepting applications from qualified students.</p>
        </div>
      </div>

      {statusMsg && (
        <div
          className="card"
          style={{
            marginBottom: 'var(--space-6)',
            padding: 'var(--space-4)',
            background: statusMsg.error ? 'rgba(196, 59, 59, 0.12)' : 'rgba(45, 140, 78, 0.12)',
            borderLeft: `3px solid ${statusMsg.error ? 'var(--danger)' : 'var(--success)'}`,
            color: statusMsg.error ? 'var(--danger)' : 'var(--success)',
          }}
        >
          {statusMsg.text}
        </div>
      )}

      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <table className="eqel-table">
          <thead>
            <tr>
              <th>Company & Job Role</th>
              <th>CTC (LPA)</th>
              <th>Status</th>
              <th>Registration Deadline</th>
              <th style={{ textAlign: 'right' }}>Application</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={5} style={{ textAlign: 'center', padding: 'var(--space-8)' }}>
                  Loading hiring drives...
                </td>
              </tr>
            ) : drives.length === 0 ? (
              <tr>
                <td colSpan={5} style={{ textAlign: 'center', padding: 'var(--space-8)', color: 'var(--ink-3)' }}>
                  No open placement drives at this time.
                </td>
              </tr>
            ) : (
              drives.map(d => {
                const applied = isApplied(d.id);
                return (
                  <tr key={d.id}>
                    <td>
                      <div style={{ fontWeight: 600, color: 'var(--ink)' }}>{d.title}</div>
                      <div style={{ fontSize: '0.8125rem', color: 'var(--ink-3)' }}>{d.jobRole}</div>
                    </td>
                    <td style={{ fontFamily: 'var(--font-mono)', fontWeight: 600 }}>
                      ₹{d.packageLpa} LPA
                    </td>
                    <td>
                      <span className="badge badge-eligible">{d.driveStatus}</span>
                    </td>
                    <td style={{ fontSize: '0.8125rem', fontFamily: 'var(--font-mono)' }}>
                      {d.registrationClose ? new Date(d.registrationClose).toLocaleDateString() : 'Rolling'}
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      {applied ? (
                        <span className="badge badge-verified">Applied</span>
                      ) : (
                        <button
                          type="button"
                          className="btn btn-primary btn-sm"
                          style={{ fontSize: '0.75rem', padding: '4px 12px', minHeight: '32px' }}
                          onClick={() => handleApply(d.id)}
                        >
                          Apply Now
                        </button>
                      )}
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
