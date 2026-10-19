import { useState, useEffect } from 'react';
import api from '../../services/api';
import type { EvaluationRecord } from '../../types';

export default function StudentEligibilityPage() {
  const [evaluations, setEvaluations] = useState<EvaluationRecord[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    api.get<EvaluationRecord[]>('/api/student/eligibility')
      .then(res => setEvaluations(res || []))
      .catch(() => setEvaluations([]))
      .finally(() => setLoading(false));
  }, []);

  return (
    <div>
      <div className="breadcrumb">
        <span>Student Portal</span>
        <span className="breadcrumb-separator">/</span>
        <span>Eligibility Verdicts</span>
      </div>

      <div className="page-header">
        <div>
          <h1 className="page-title">Evidence-Qualified Eligibility Results</h1>
          <p className="page-subtitle">Granular explanation of your qualification status for corporate recruitment campaigns.</p>
        </div>
      </div>

      {loading ? (
        <div className="card" style={{ textAlign: 'center', padding: 'var(--space-8)' }}>
          Loading your eligibility ledger...
        </div>
      ) : evaluations.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: 'var(--space-8)', color: 'var(--ink-3)' }}>
          No eligibility evaluations recorded yet. When a Placement Officer evaluates your profile, the explainable decision will appear here.
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 'var(--space-6)' }}>
          {evaluations.map(ev => (
            <div key={ev.id} className="card" style={{ border: '1.5px solid var(--line)' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 'var(--space-4)', borderBottom: '1px solid var(--line)', paddingBottom: 'var(--space-3)' }}>
                <div>
                  <span style={{ fontFamily: 'var(--font-mono)', fontSize: '0.75rem', color: 'var(--ink-3)' }}>
                    Evaluation #{ev.id} · Decision Engine v{ev.decisionEngineVersion}
                  </span>
                  <h3 style={{ fontSize: '1.35rem', marginTop: 'var(--space-1)' }}>
                    Verdict:{' '}
                    <span className={`badge ${
                      ev.overallOutcome === 'ELIGIBLE' ? 'badge-eligible' :
                      ev.overallOutcome === 'INELIGIBLE' ? 'badge-ineligible' : 'badge-unverifiable'
                    }`} style={{ fontSize: '1rem', padding: '2px 10px' }}>
                      {ev.overallOutcome}
                    </span>
                  </h3>
                </div>
                <div style={{ fontFamily: 'var(--font-mono)', fontSize: '0.8125rem', color: 'var(--ink-3)' }}>
                  {new Date(ev.evaluatedAt).toLocaleString()}
                </div>
              </div>

              <p style={{ fontSize: '0.938rem', color: 'var(--ink-2)', marginBottom: 'var(--space-4)' }}>
                <strong>Explanation:</strong> {ev.explanation}
              </p>

              {ev.ruleResults && ev.ruleResults.length > 0 && (
                <div>
                  <h4 style={{ fontSize: '0.875rem', textTransform: 'uppercase', color: 'var(--ink-3)', marginBottom: 'var(--space-2)', fontFamily: 'var(--font-mono)' }}>
                    Rule Evaluation Breakdown:
                  </h4>
                  <table className="eqel-table">
                    <thead>
                      <tr>
                        <th>Rule</th>
                        <th>Status</th>
                        <th>Your Evidence</th>
                        <th>Required Threshold</th>
                        <th>Explanation</th>
                      </tr>
                    </thead>
                    <tbody>
                      {ev.ruleResults.map(rr => (
                        <tr key={rr.id || rr.ruleCode}>
                          <td style={{ fontFamily: 'var(--font-mono)', fontWeight: 600 }}>{rr.ruleCode}</td>
                          <td>
                            <span className={`badge ${
                              rr.result === 'SATISFIED' ? 'badge-eligible' :
                              rr.result === 'VIOLATED' ? 'badge-ineligible' : 'badge-unverifiable'
                            }`}>
                              {rr.result}
                            </span>
                          </td>
                          <td style={{ fontFamily: 'var(--font-mono)' }}>{rr.evidenceValue || 'None'}</td>
                          <td style={{ fontFamily: 'var(--font-mono)' }}>{rr.thresholdValue}</td>
                          <td style={{ fontSize: '0.8125rem' }}>{rr.explanation}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
