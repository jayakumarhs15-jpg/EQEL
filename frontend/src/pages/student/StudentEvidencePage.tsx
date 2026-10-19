import { useState, useEffect, type FormEvent } from 'react';
import api from '../../services/api';
import type { EvidenceRecord } from '../../types';

export default function StudentEvidencePage() {
  const [evidenceList, setEvidenceList] = useState<EvidenceRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);

  // New evidence form
  const [evidenceType, setEvidenceType] = useState('TRANSCRIPT');
  const [fieldName, setFieldName] = useState('CGPA');
  const [fieldValue, setFieldValue] = useState('');
  const [source, setSource] = useState('Official Semester 6 Grade Card PDF');

  const fetchEvidence = () => {
    setLoading(true);
    api.get<EvidenceRecord[]>('/api/student/evidence')
      .then(res => setEvidenceList(res || []))
      .catch(() => setEvidenceList([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchEvidence();
  }, []);

  const handleSubmitEvidence = async (e: FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/api/student/evidence', {
        evidenceType,
        fieldName,
        fieldValue,
        source,
      });

      setShowModal(false);
      setFieldValue('');
      fetchEvidence();
    } catch {
      alert('Failed to submit evidence.');
    }
  };

  return (
    <div>
      <div className="breadcrumb">
        <span>Student Portal</span>
        <span className="breadcrumb-separator">/</span>
        <span>My Evidence</span>
      </div>

      <div className="page-header">
        <div>
          <h1 className="page-title">Submitted Evidence & Proof</h1>
          <p className="page-subtitle">Evidence claims submitted to substantiate placement policy compliance. Must be verified by Placement Officers.</p>
        </div>
        <button
          type="button"
          className="btn btn-primary btn-sm"
          onClick={() => setShowModal(true)}
        >
          + Submit Academic Proof
        </button>
      </div>

      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <table className="eqel-table">
          <thead>
            <tr>
              <th>Target Rule Field</th>
              <th>Claimed Value</th>
              <th>Verification Status</th>
              <th>Source & Revision</th>
              <th>Reviewer Feedback</th>
              <th>Date</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} style={{ textAlign: 'center', padding: 'var(--space-8)' }}>
                  Loading evidence records...
                </td>
              </tr>
            ) : evidenceList.length === 0 ? (
              <tr>
                <td colSpan={6} style={{ textAlign: 'center', padding: 'var(--space-8)', color: 'var(--ink-3)' }}>
                  No evidence records submitted yet. Click "+ Submit Academic Proof" to upload credentials.
                </td>
              </tr>
            ) : (
              evidenceList.map(e => (
                <tr key={e.id}>
                  <td>
                    <div style={{ fontWeight: 600, color: 'var(--ink)' }}>{e.fieldName}</div>
                    <div style={{ fontSize: '0.8125rem', color: 'var(--ink-3)' }}>{e.evidenceType}</div>
                  </td>
                  <td style={{ fontFamily: 'var(--font-mono)', fontWeight: 600 }}>{e.fieldValue}</td>
                  <td>
                    <span className={`badge ${
                      e.status === 'VERIFIED' ? 'badge-verified' :
                      e.status === 'REJECTED' ? 'badge-ineligible' : 'badge-pending'
                    }`}>
                      {e.status}
                    </span>
                  </td>
                  <td>
                    <div style={{ fontSize: '0.875rem' }}>{e.source}</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--ink-3)', fontFamily: 'var(--font-mono)' }}>Rev #{e.revision}</div>
                  </td>
                  <td style={{ fontSize: '0.875rem', color: 'var(--ink-2)' }}>
                    {e.reviewerNotes || 'Pending officer review'}
                  </td>
                  <td style={{ fontSize: '0.8125rem', fontFamily: 'var(--font-mono)' }}>
                    {new Date(e.submittedAt).toLocaleDateString()}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {showModal && (
        <div className="modal-backdrop" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={ev => ev.stopPropagation()}>
            <h2 className="modal-title">Submit Supporting Evidence</h2>
            <form onSubmit={handleSubmitEvidence} className="login-form">
              <div className="form-group">
                <label className="form-label">Evidence Category</label>
                <select
                  className="form-select"
                  value={evidenceType}
                  onChange={e => setEvidenceType(e.target.value)}
                >
                  <option value="TRANSCRIPT">Official Academic Transcript</option>
                  <option value="CERTIFICATE">Industry Certificate</option>
                  <option value="PROJECT">Portfolio / Capstone Repository</option>
                  <option value="NO_BACKLOG_CERT">No Backlogs Attestation</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label">Rule Field Name</label>
                <input
                  type="text"
                  className="form-input"
                  required
                  value={fieldName}
                  onChange={e => setFieldName(e.target.value)}
                  placeholder="e.g. CGPA, BACKLOGS, DEGREE"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Substantiated Value</label>
                <input
                  type="text"
                  className="form-input"
                  required
                  value={fieldValue}
                  onChange={e => setFieldValue(e.target.value)}
                  placeholder="e.g. 8.65"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Document Citation / Reference Source</label>
                <input
                  type="text"
                  className="form-input"
                  required
                  value={source}
                  onChange={e => setSource(e.target.value)}
                  placeholder="e.g. Official University Portal Gradecard Sem 6"
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
                  Submit for Verification
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
