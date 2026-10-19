import { useState, useEffect } from 'react';
import api from '../../services/api';
import type { EvaluationRecord, Page } from '../../types';

export default function PlacementHistoryPage() {
  const [history, setHistory] = useState<EvaluationRecord[]>([]);
  const [loading, setLoading] = useState(true);
  const [outcomeFilter, setOutcomeFilter] = useState('');

  const fetchHistory = () => {
    setLoading(true);
    const params = new URLSearchParams({ size: '15' });
    if (outcomeFilter) params.set('outcome', outcomeFilter);

    api.get<Page<EvaluationRecord>>(`/api/placement/evaluations/history?${params.toString()}`)
      .then(res => setHistory(res.content || []))
      .catch(() => setHistory([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchHistory();
  }, [outcomeFilter]);

  return (
    <div>
      <div className="breadcrumb">
        <span>Placement Office</span>
        <span className="breadcrumb-separator">/</span>
        <span>Decision Ledger</span>
      </div>

      <div className="page-header">
        <div>
          <h1 className="page-title">Eligibility Decision Ledger</h1>
          <p className="page-subtitle">Historical, immutable archive of all generated eligibility evaluations with input snapshots.</p>
        </div>
      </div>

      <div className="card" style={{ padding: 'var(--space-4)', marginBottom: 'var(--space-6)' }}>
        <div style={{ display: 'flex', gap: 'var(--space-4)', alignItems: 'center' }}>
          <span style={{ fontSize: '0.875rem', fontWeight: 600 }}>Filter by Outcome:</span>
          <select
            className="form-select"
            value={outcomeFilter}
            onChange={e => setOutcomeFilter(e.target.value)}
            style={{ width: '180px' }}
          >
            <option value="">All Outcomes</option>
            <option value="ELIGIBLE">Eligible</option>
            <option value="INELIGIBLE">Ineligible</option>
            <option value="UNVERIFIABLE">Unverifiable</option>
          </select>
        </div>
      </div>

      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <table className="eqel-table">
          <thead>
            <tr>
              <th>Evaluation ID</th>
              <th>Outcome</th>
              <th>Explanation & Findings</th>
              <th>Engine Version</th>
              <th>Timestamp</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={5} style={{ textAlign: 'center', padding: 'var(--space-8)' }}>
                  Loading decision ledger...
                </td>
              </tr>
            ) : history.length === 0 ? (
              <tr>
                <td colSpan={5} style={{ textAlign: 'center', padding: 'var(--space-8)', color: 'var(--ink-3)' }}>
                  No historical evaluations recorded yet. Run an evaluation to record a ledger entry.
                </td>
              </tr>
            ) : (
              history.map(item => (
                <tr key={item.id}>
                  <td style={{ fontFamily: 'var(--font-mono)', fontWeight: 600 }}>
                    #{item.id}
                  </td>
                  <td>
                    <span className={`badge ${
                      item.overallOutcome === 'ELIGIBLE' ? 'badge-eligible' :
                      item.overallOutcome === 'INELIGIBLE' ? 'badge-ineligible' : 'badge-unverifiable'
                    }`}>
                      {item.overallOutcome}
                    </span>
                  </td>
                  <td style={{ fontSize: '0.875rem' }}>
                    {item.explanation}
                  </td>
                  <td style={{ fontFamily: 'var(--font-mono)', fontSize: '0.8125rem' }}>
                    v{item.decisionEngineVersion}
                  </td>
                  <td style={{ fontFamily: 'var(--font-mono)', fontSize: '0.8125rem' }}>
                    {new Date(item.evaluatedAt).toLocaleString()}
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
