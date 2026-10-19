import { useState, useEffect } from 'react';
import api from '../../services/api';
import type { EvidenceRecord, Page } from '../../types';

export default function PlacementEvidencePage() {
  const [evidenceList, setEvidenceList] = useState<EvidenceRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [statusFilter, setStatusFilter] = useState('PENDING');

  const fetchEvidence = () => {
    setLoading(true);
    api.get<Page<EvidenceRecord>>(`/api/placement/evidence/pending?status=${statusFilter}`)
      .then(res => setEvidenceList(res.content || []))
      .catch(() => setEvidenceList([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchEvidence();
  }, [statusFilter]);

  const handleVerify = async (id: number, status: 'VERIFIED' | 'REJECTED') => {
    const notes = prompt(`Enter reviewer notes for ${status.toLowerCase()} mark:`) || 'Reviewed by Placement Office';
    try {
      await api.put(`/api/placement/evidence/${id}/verify`, { status, notes });
      fetchEvidence();
    } catch (err: unknown) {
      const errorObj = err as { message?: string };
      alert(errorObj?.message || 'Verification update failed.');
    }
  };

  return (
    <div>
      <div className="breadcrumb">
        <span>Placement Office</span>
        <span className="breadcrumb-separator">/</span>
        <span>Evidence Verification</span>
      </div>

      <div className="page-header">
        <div>
          <h1 className="page-title">Candidate Evidence Review Queue</h1>
          <p className="page-subtitle">Review submitted academic evidence, certificates, and transcripts. Approved evidence updates eligibility calculations.</p>
        </div>
      </div>

      <div className="card" style={{ padding: 'var(--space-4)', marginBottom: 'var(--space-6)' }}>
        <div style={{ display: 'flex', gap: 'var(--space-4)', alignItems: 'center' }}>
          <span style={{ fontSize: '0.875rem', fontWeight: 600 }}>Filter by Status:</span>
          <select
            className="form-select"
            value={statusFilter}
            onChange={e => setStatusFilter(e.target.value)}
            style={{ width: '180px' }}
          >
            <option value="PENDING">Pending Review</option>
            <option value="VERIFIED">Verified</option>
            <option value="REJECTED">Rejected</option>
            <option value="CONFLICTING">Conflicting</option>
          </select>
        </div>
      </div>

      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <table className="eqel-table">
          <thead>
            <tr>
              <th>Evidence Claim</th>
              <th>Submitted Value</th>
              <th>Source & Revision</th>
              <th>Status</th>
              <th>Submitted At</th>
              <th style={{ textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} style={{ textAlign: 'center', padding: 'var(--space-8)' }}>
                  Loading evidence claims...
                </td>
              </tr>
            ) : evidenceList.length === 0 ? (
              <tr>
                <td colSpan={6} style={{ textAlign: 'center', padding: 'var(--space-8)', color: 'var(--ink-3)' }}>
                  No evidence records matching status: {statusFilter}.
                </td>
              </tr>
            ) : (
              evidenceList.map(item => (
                <tr key={item.id}>
                  <td>
                    <div style={{ fontWeight: 600, color: 'var(--ink)' }}>{item.fieldName}</div>
                    <div style={{ fontSize: '0.8125rem', color: 'var(--ink-3)' }}>{item.evidenceType}</div>
                  </td>
                  <td style={{ fontFamily: 'var(--font-mono)', fontWeight: 600 }}>
                    {item.fieldValue}
                  </td>
                  <td>
                    <div style={{ fontSize: '0.875rem' }}>{item.source || 'Manual Submission'}</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--ink-3)', fontFamily: 'var(--font-mono)' }}>Rev #{item.revision}</div>
                  </td>
                  <td>
                    <span className={`badge ${
                      item.status === 'VERIFIED' ? 'badge-verified' :
                      item.status === 'REJECTED' ? 'badge-ineligible' : 'badge-pending'
                    }`}>
                      {item.status}
                    </span>
                  </td>
                  <td style={{ fontSize: '0.8125rem', fontFamily: 'var(--font-mono)' }}>
                    {new Date(item.submittedAt).toLocaleDateString()}
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    {item.status === 'PENDING' ? (
                      <div style={{ display: 'flex', gap: 'var(--space-2)', justifyContent: 'flex-end' }}>
                        <button
                          type="button"
                          className="btn btn-primary btn-sm"
                          style={{ fontSize: '0.75rem', padding: '2px 10px', minHeight: '28px' }}
                          onClick={() => handleVerify(item.id, 'VERIFIED')}
                        >
                          Approve
                        </button>
                        <button
                          type="button"
                          className="btn btn-outline btn-sm"
                          style={{ fontSize: '0.75rem', padding: '2px 10px', minHeight: '28px' }}
                          onClick={() => handleVerify(item.id, 'REJECTED')}
                        >
                          Reject
                        </button>
                      </div>
                    ) : (
                      <span style={{ fontSize: '0.75rem', color: 'var(--ink-3)' }}>Completed</span>
                    )}
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
