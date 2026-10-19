import { useState, useEffect, type FormEvent } from 'react';
import api from '../../services/api';
import type { EligibilityPolicy } from '../../types';

export default function PlacementPoliciesPage() {
  const [policies, setPolicies] = useState<EligibilityPolicy[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);

  // New policy state
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [minCgpa, setMinCgpa] = useState('7.50');
  const [maxBacklogs, setMaxBacklogs] = useState('0');
  const [departments, setDepartments] = useState('CSE, IT, ECE');

  const fetchPolicies = () => {
    setLoading(true);
    api.get<EligibilityPolicy[]>('/api/placement/policies')
      .then(res => setPolicies(res || []))
      .catch(() => setPolicies([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchPolicies();
  }, []);

  const handleCreatePolicy = async (e: FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/api/placement/policies', {
        name,
        description,
        rules: [
          {
            ruleCode: 'MIN_CGPA',
            ruleDescription: `Minimum CGPA of ${minCgpa}`,
            fieldName: 'CGPA',
            operator: 'GTE',
            thresholdValue: minCgpa,
            isMandatory: true,
          },
          {
            ruleCode: 'MAX_BACKLOGS',
            ruleDescription: `Max active backlogs: ${maxBacklogs}`,
            fieldName: 'BACKLOGS',
            operator: 'LTE',
            thresholdValue: maxBacklogs,
            isMandatory: true,
          },
          {
            ruleCode: 'ELIGIBLE_DEPTS',
            ruleDescription: `Allowed Departments: ${departments}`,
            fieldName: 'DEPARTMENT',
            operator: 'IN',
            thresholdValue: departments,
            isMandatory: true,
          },
        ],
      });

      setShowModal(false);
      setName('');
      setDescription('');
      fetchPolicies();
    } catch (err: unknown) {
      const errorObj = err as { message?: string };
      alert(errorObj?.message || 'Failed to create policy');
    }
  };

  return (
    <div>
      <div className="breadcrumb">
        <span>Placement Office</span>
        <span className="breadcrumb-separator">/</span>
        <span>Eligibility Policies</span>
      </div>

      <div className="page-header">
        <div>
          <h1 className="page-title">Versioned Eligibility Policies</h1>
          <p className="page-subtitle">Define explicit rules for corporate recruitment. Modifications generate immutable revision snapshots.</p>
        </div>
        <button
          type="button"
          className="btn btn-primary btn-sm"
          onClick={() => setShowModal(true)}
        >
          + Author New Policy
        </button>
      </div>

      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <table className="eqel-table">
          <thead>
            <tr>
              <th>Policy Profile</th>
              <th>Version Status</th>
              <th>State</th>
              <th>Created</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={4} style={{ textAlign: 'center', padding: 'var(--space-8)' }}>
                  Loading policies...
                </td>
              </tr>
            ) : policies.length === 0 ? (
              <tr>
                <td colSpan={4} style={{ textAlign: 'center', padding: 'var(--space-8)', color: 'var(--ink-3)' }}>
                  No eligibility policies defined yet. Click "+ Author New Policy" above.
                </td>
              </tr>
            ) : (
              policies.map(p => (
                <tr key={p.id}>
                  <td>
                    <div style={{ fontWeight: 600, color: 'var(--ink)' }}>{p.name}</div>
                    <div style={{ fontSize: '0.8125rem', color: 'var(--ink-3)' }}>{p.description || 'Standard corporate eligibility criteria'}</div>
                  </td>
                  <td>
                    <span className="badge badge-verified">v1.0 (Locked Snapshot)</span>
                  </td>
                  <td>
                    <span className="badge badge-eligible">{p.isActive ? 'Active' : 'Archived'}</span>
                  </td>
                  <td style={{ fontSize: '0.8125rem', fontFamily: 'var(--font-mono)' }}>
                    {p.createdAt ? new Date(p.createdAt).toLocaleDateString() : 'Active'}
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
            <h2 className="modal-title">Author Versioned Policy</h2>
            <form onSubmit={handleCreatePolicy} className="login-form">
              <div className="form-group">
                <label className="form-label">Policy Name</label>
                <input
                  type="text"
                  className="form-input"
                  required
                  value={name}
                  onChange={e => setName(e.target.value)}
                  placeholder="e.g. Tier-1 Software Engineering Standard"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Description / Rationale</label>
                <textarea
                  className="form-textarea"
                  value={description}
                  onChange={e => setDescription(e.target.value)}
                  placeholder="Applies to product engineering roles requiring baseline academic excellence..."
                />
              </div>

              <div style={{ borderTop: '1px solid var(--line)', paddingTop: 'var(--space-4)', marginTop: 'var(--space-2)' }}>
                <span style={{ fontSize: '0.8125rem', fontWeight: 700, textTransform: 'uppercase', color: 'var(--ink-2)' }}>
                  Mandatory Rules (v1)
                </span>

                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 'var(--space-4)', marginTop: 'var(--space-3)' }}>
                  <div className="form-group">
                    <label className="form-label">Min CGPA (≥)</label>
                    <input
                      type="number"
                      step="0.01"
                      className="form-input"
                      required
                      value={minCgpa}
                      onChange={e => setMinCgpa(e.target.value)}
                    />
                  </div>

                  <div className="form-group">
                    <label className="form-label">Max Active Backlogs (≤)</label>
                    <input
                      type="number"
                      className="form-input"
                      required
                      value={maxBacklogs}
                      onChange={e => setMaxBacklogs(e.target.value)}
                    />
                  </div>
                </div>

                <div className="form-group" style={{ marginTop: 'var(--space-4)' }}>
                  <label className="form-label">Eligible Departments (Comma separated)</label>
                  <input
                    type="text"
                    className="form-input"
                    required
                    value={departments}
                    onChange={e => setDepartments(e.target.value)}
                  />
                </div>
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
                  Publish Policy v1
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
