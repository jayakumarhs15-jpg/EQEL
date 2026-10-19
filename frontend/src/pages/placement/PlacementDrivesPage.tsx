import { useState, useEffect, type FormEvent } from 'react';
import api from '../../services/api';
import type { PlacementDrive, Page } from '../../types';

export default function PlacementDrivesPage() {
  const [drives, setDrives] = useState<PlacementDrive[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);

  // New drive form
  const [title, setTitle] = useState('');
  const [jobRole, setJobRole] = useState('');
  const [packageLpa, setPackageLpa] = useState('14.50');
  const [description, setDescription] = useState('');

  const fetchDrives = () => {
    setLoading(true);
    api.get<Page<PlacementDrive>>('/api/placement/drives')
      .then(res => setDrives(res.content || []))
      .catch(() => setDrives([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchDrives();
  }, []);

  const handleCreateDrive = async (e: FormEvent) => {
    e.preventDefault();
    try {
      // First ensure a company exists or create one
      const companyRes = await api.post<{ id: number }>('/api/placement/companies', {
        name: title.includes(' ') ? title.split(' ')[0] + ' Corporation' : 'Leading Tech Corp',
        industry: 'Technology / Software',
      });

      await api.post('/api/placement/drives', {
        companyId: companyRes.id,
        title,
        jobRole,
        packageLpa: parseFloat(packageLpa),
        description,
        driveStatus: 'OPEN',
      });

      setShowModal(false);
      setTitle('');
      setJobRole('');
      fetchDrives();
    } catch (err: unknown) {
      const errorObj = err as { message?: string };
      alert(errorObj?.message || 'Failed to create drive');
    }
  };

  return (
    <div>
      <div className="breadcrumb">
        <span>Placement Office</span>
        <span className="breadcrumb-separator">/</span>
        <span>Companies & Drives</span>
      </div>

      <div className="page-header">
        <div>
          <h1 className="page-title">Placement Hiring Drives</h1>
          <p className="page-subtitle">Schedule recruitment campaigns, assign versioned criteria, and monitor registration windows.</p>
        </div>
        <button
          type="button"
          className="btn btn-primary btn-sm"
          onClick={() => setShowModal(true)}
        >
          + Schedule New Drive
        </button>
      </div>

      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <table className="eqel-table">
          <thead>
            <tr>
              <th>Drive Name & Role</th>
              <th>Package (LPA)</th>
              <th>Status</th>
              <th>Registration Closes</th>
              <th style={{ textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={5} style={{ textAlign: 'center', padding: 'var(--space-8)' }}>
                  Loading drives...
                </td>
              </tr>
            ) : drives.length === 0 ? (
              <tr>
                <td colSpan={5} style={{ textAlign: 'center', padding: 'var(--space-8)', color: 'var(--ink-3)' }}>
                  No active drives. Click "+ Schedule New Drive" to initialize a recruitment campaign.
                </td>
              </tr>
            ) : (
              drives.map(d => (
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
                    <a
                      href={`/placement/evaluate?driveId=${d.id}`}
                      className="btn btn-outline btn-sm"
                      style={{ fontSize: '0.75rem' }}
                    >
                      Evaluate Candidates
                    </a>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {showModal && (
        <div className="modal-backdrop" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h2 className="modal-title">Schedule Placement Drive</h2>
            <form onSubmit={handleCreateDrive} className="login-form">
              <div className="form-group">
                <label className="form-label">Drive Title</label>
                <input
                  type="text"
                  className="form-input"
                  required
                  value={title}
                  onChange={e => setTitle(e.target.value)}
                  placeholder="e.g. Google Software Engineering 2026"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Job Role</label>
                <input
                  type="text"
                  className="form-input"
                  required
                  value={jobRole}
                  onChange={e => setJobRole(e.target.value)}
                  placeholder="e.g. Associate Software Development Engineer"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Compensation Package (CTC in LPA)</label>
                <input
                  type="number"
                  step="0.1"
                  className="form-input"
                  required
                  value={packageLpa}
                  onChange={e => setPackageLpa(e.target.value)}
                  placeholder="14.5"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Drive Overview</label>
                <textarea
                  className="form-textarea"
                  value={description}
                  onChange={e => setDescription(e.target.value)}
                  placeholder="Brief description of requirements, interview stages, and expectations..."
                />
              </div>

              <div className="modal-actions">
                <button
                  type="button"
                  className="btn btn-ghost"
                  onClick={() => setShowModal(false)}
                >
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Launch Drive
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
