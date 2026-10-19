import { useState, useEffect, type FormEvent } from 'react';
import { useSearchParams } from 'react-router-dom';
import api from '../../services/api';
import type { StudentProfile, PlacementDrive, EvaluationRecord, Page } from '../../types';

export default function PlacementEvaluatePage() {
  const [searchParams] = useSearchParams();
  const initialStudentId = searchParams.get('studentId') || '';
  const initialDriveId = searchParams.get('driveId') || '';

  const [students, setStudents] = useState<StudentProfile[]>([]);
  const [drives, setDrives] = useState<PlacementDrive[]>([]);
  const [selectedStudentId, setSelectedStudentId] = useState<string>(initialStudentId);
  const [selectedDriveId, setSelectedDriveId] = useState<string>(initialDriveId);

  const [evaluating, setEvaluating] = useState<boolean>(false);
  const [result, setResult] = useState<EvaluationRecord | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // Load available students and drives
    api.get<Page<StudentProfile>>('/api/placement/students?size=50')
      .then(res => setStudents(res.content || []))
      .catch(() => {});

    api.get<Page<PlacementDrive>>('/api/placement/drives?size=50')
      .then(res => setDrives(res.content || []))
      .catch(() => {});
  }, []);

  const handleEvaluate = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setResult(null);

    if (!selectedStudentId || !selectedDriveId) {
      setError('Please select both a candidate and a placement drive.');
      return;
    }

    setEvaluating(true);
    try {
      const evaluation = await api.post<EvaluationRecord>('/api/placement/evaluations/evaluate', {
        studentId: Number(selectedStudentId),
        driveId: Number(selectedDriveId),
      });
      setResult(evaluation);
    } catch (err: unknown) {
      const errorObj = err as { message?: string };
      setError(errorObj?.message || 'Eligibility evaluation failed.');
    } finally {
      setEvaluating(false);
    }
  };

  return (
    <div>
      <div className="breadcrumb">
        <span>Placement Office</span>
        <span className="breadcrumb-separator">/</span>
        <span>Evaluate Eligibility</span>
      </div>

      <div className="page-header">
        <div>
          <h1 className="page-title">Eligibility Decision Engine (v1.0.0)</h1>
          <p className="page-subtitle">Execute deterministic rule evaluation, explain granular rule results, and record verdicts into the ledger.</p>
        </div>
      </div>

      {/* ── Selection Form ── */}
      <div className="card" style={{ marginBottom: 'var(--space-8)' }}>
        <form onSubmit={handleEvaluate} style={{ display: 'grid', gridTemplateColumns: '1fr 1fr auto', gap: 'var(--space-4)', alignItems: 'flex-end' }}>
          <div className="form-group">
            <label className="form-label">Select Student Candidate</label>
            <select
              className="form-select"
              required
              value={selectedStudentId}
              onChange={e => setSelectedStudentId(e.target.value)}
            >
              <option value="">-- Choose Candidate --</option>
              {students.map(s => (
                <option key={s.id} value={s.id}>
                  {s.enrollmentNumber} — {s.fullName || 'Student'} ({s.department}, CGPA: {s.cgpa || 'N/A'}, Backlogs: {s.backlogs})
                </option>
              ))}
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Select Placement Drive</label>
            <select
              className="form-select"
              required
              value={selectedDriveId}
              onChange={e => setSelectedDriveId(e.target.value)}
            >
              <option value="">-- Choose Drive --</option>
              {drives.map(d => (
                <option key={d.id} value={d.id}>
                  {d.title} ({d.jobRole}) — ₹{d.packageLpa} LPA
                </option>
              ))}
            </select>
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            disabled={evaluating}
            style={{ height: '44px' }}
          >
            {evaluating ? 'Evaluating...' : '⚡ Run Evaluation'}
          </button>
        </form>
      </div>

      {error && (
        <div className="login-error-alert" style={{ marginBottom: 'var(--space-6)' }}>
          {error}
        </div>
      )}

      {/* ── Evaluation Result Card ── */}
      {result && (
        <div className="card" style={{ border: '2px solid var(--line)' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 'var(--space-6)', borderBottom: '1px solid var(--line)', paddingBottom: 'var(--space-4)' }}>
            <div>
              <span style={{ fontFamily: 'var(--font-mono)', fontSize: '0.75rem', color: 'var(--ink-3)', textTransform: 'uppercase' }}>
                Evaluation Record #{result.id} · Decision Engine v{result.decisionEngineVersion}
              </span>
              <h2 style={{ fontSize: '1.75rem', marginTop: 'var(--space-1)' }}>
                Overall Verdict:{' '}
                <span className={`badge ${
                  result.overallOutcome === 'ELIGIBLE' ? 'badge-eligible' :
                  result.overallOutcome === 'INELIGIBLE' ? 'badge-ineligible' : 'badge-unverifiable'
                }`} style={{ fontSize: '1.25rem', padding: '4px 14px' }}>
                  {result.overallOutcome}
                </span>
              </h2>
            </div>
            <div style={{ textAlign: 'right', fontFamily: 'var(--font-mono)', fontSize: '0.8125rem', color: 'var(--ink-3)' }}>
              Evaluated: {new Date(result.evaluatedAt).toLocaleString()}
            </div>
          </div>

          <p style={{ fontSize: '1.05rem', color: 'var(--ink-2)', marginBottom: 'var(--space-6)' }}>
            <strong>Engine Rationale:</strong> {result.explanation}
          </p>

          <h3 style={{ fontSize: '1.125rem', marginBottom: 'var(--space-3)' }}>Rule-by-Rule Breakdown</h3>
          <div style={{ overflowX: 'auto' }}>
            <table className="eqel-table">
              <thead>
                <tr>
                  <th>Rule Code</th>
                  <th>Outcome</th>
                  <th>Candidate Evidence</th>
                  <th>Policy Threshold</th>
                  <th>Operational Explanation</th>
                </tr>
              </thead>
              <tbody>
                {result.ruleResults && result.ruleResults.length > 0 ? (
                  result.ruleResults.map(rr => (
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
                      <td style={{ fontSize: '0.875rem' }}>{rr.explanation}</td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={5} style={{ textAlign: 'center', padding: 'var(--space-4)' }}>
                      No individual rule result details recorded.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
