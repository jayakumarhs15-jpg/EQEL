import { useState, useEffect } from 'react';
import api from '../../services/api';
import type { AuditEvent, Page } from '../../types';

export default function AdminAuditPage() {
  const [logs, setLogs] = useState<AuditEvent[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const fetchLogs = () => {
    setLoading(true);
    api.get<Page<AuditEvent>>(`/api/admin/audit-logs?page=${page}&size=15`)
      .then((res) => {
        setLogs(res.content || []);
        setTotalPages(res.totalPages || 1);
      })
      .catch(() => setLogs([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchLogs();
  }, [page]);

  return (
    <div>
      <div className="breadcrumb">
        <span>Administration</span>
        <span className="breadcrumb-separator">/</span>
        <span>Audit Logs</span>
      </div>

      <div className="page-header">
        <div>
          <h1 className="page-title">Security & Governance Audit Trail</h1>
          <p className="page-subtitle">Immutable chronological log of all administrative actions, role updates, and password changes.</p>
        </div>
      </div>

      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <div style={{ overflowX: 'auto' }}>
          <table className="eqel-table">
            <thead>
              <tr>
                <th>Timestamp</th>
                <th>Action</th>
                <th>Target Entity</th>
                <th>Details & Rationale</th>
                <th>Client IP</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={5} style={{ textAlign: 'center', padding: 'var(--space-8)' }}>
                    Loading audit events...
                  </td>
                </tr>
              ) : logs.length === 0 ? (
                <tr>
                  <td colSpan={5} style={{ textAlign: 'center', padding: 'var(--space-8)', color: 'var(--ink-3)' }}>
                    No administrative audit events recorded yet.
                  </td>
                </tr>
              ) : (
                logs.map((log) => (
                  <tr key={log.id}>
                    <td style={{ fontFamily: 'var(--font-mono)', fontSize: '0.8125rem' }}>
                      {new Date(log.createdAt).toLocaleString()}
                    </td>
                    <td>
                      <span className="badge badge-pending">{log.action}</span>
                    </td>
                    <td style={{ fontFamily: 'var(--font-mono)', fontSize: '0.8125rem' }}>
                      {log.entityType} #{log.entityId}
                    </td>
                    <td style={{ fontSize: '0.875rem', color: 'var(--ink)' }}>
                      {log.details}
                    </td>
                    <td style={{ fontFamily: 'var(--font-mono)', fontSize: '0.75rem', color: 'var(--ink-3)' }}>
                      {log.ipAddress}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {totalPages > 1 && (
          <div className="pagination">
            <button
              type="button"
              className="pagination-btn"
              disabled={page === 0}
              onClick={() => setPage(p => p - 1)}
            >
              ←
            </button>
            <span style={{ fontSize: '0.875rem', color: 'var(--ink-3)' }}>
              Page {page + 1} of {totalPages}
            </span>
            <button
              type="button"
              className="pagination-btn"
              disabled={page >= totalPages - 1}
              onClick={() => setPage(p => p + 1)}
            >
              →
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
