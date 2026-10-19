import { useState, useEffect, type FormEvent } from 'react';
import api from '../../services/api';
import type { StudentProfile, Page } from '../../types';

export default function PlacementStudentsPage() {
  const [students, setStudents] = useState<StudentProfile[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const fetchStudents = () => {
    setLoading(true);
    const params = new URLSearchParams({ page: page.toString(), size: '10' });
    if (search) params.set('query', search);

    api.get<Page<StudentProfile>>(`/api/placement/students?${params.toString()}`)
      .then(res => {
        setStudents(res.content || []);
        setTotalPages(res.totalPages || 1);
      })
      .catch(() => setStudents([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchStudents();
  }, [page]);

  const handleSearch = (e: FormEvent) => {
    e.preventDefault();
    setPage(0);
    fetchStudents();
  };

  return (
    <div>
      <div className="breadcrumb">
        <span>Placement Office</span>
        <span className="breadcrumb-separator">/</span>
        <span>Student Directory</span>
      </div>

      <div className="page-header">
        <div>
          <h1 className="page-title">Candidate Directory</h1>
          <p className="page-subtitle">Inspect student academic baseline records, CGPA, backlogs, and department assignments.</p>
        </div>
      </div>

      <div className="card" style={{ padding: 'var(--space-4)', marginBottom: 'var(--space-6)' }}>
        <form onSubmit={handleSearch} style={{ display: 'flex', gap: 'var(--space-4)', alignItems: 'center' }}>
          <div className="search-bar" style={{ maxWidth: '340px' }}>
            <span className="search-bar-icon">🔍</span>
            <input
              type="text"
              placeholder="Search by enrollment number or department..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
          <button type="submit" className="btn btn-outline btn-sm">
            Search
          </button>
        </form>
      </div>

      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <table className="eqel-table">
          <thead>
            <tr>
              <th>Enrollment & Student</th>
              <th>Department & Program</th>
              <th>Semester</th>
              <th>CGPA</th>
              <th>Backlogs</th>
              <th style={{ textAlign: 'right' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={6} style={{ textAlign: 'center', padding: 'var(--space-8)' }}>
                  Loading candidate directory...
                </td>
              </tr>
            ) : students.length === 0 ? (
              <tr>
                <td colSpan={6} style={{ textAlign: 'center', padding: 'var(--space-8)', color: 'var(--ink-3)' }}>
                  No student records found in candidate directory.
                </td>
              </tr>
            ) : (
              students.map(s => (
                <tr key={s.id}>
                  <td>
                    <div style={{ fontWeight: 600, color: 'var(--ink)' }}>{s.enrollmentNumber}</div>
                    <div style={{ fontSize: '0.8125rem', color: 'var(--ink-3)' }}>{s.fullName || s.email}</div>
                  </td>
                  <td>
                    <div style={{ fontWeight: 500 }}>{s.department || 'General Engineering'}</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--ink-3)' }}>{s.program || 'B.Tech'}</div>
                  </td>
                  <td style={{ fontFamily: 'var(--font-mono)' }}>Sem {s.semester || '7'}</td>
                  <td>
                    <span className="badge badge-verified" style={{ fontFamily: 'var(--font-mono)', fontSize: '0.8125rem' }}>
                      {s.cgpa ? Number(s.cgpa).toFixed(2) : 'N/A'}
                    </span>
                  </td>
                  <td>
                    <span className={`badge ${s.backlogs === 0 ? 'badge-eligible' : 'badge-ineligible'}`}>
                      {s.backlogs} {s.backlogs === 1 ? 'Backlog' : 'Backlogs'}
                    </span>
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    <a
                      href={`/placement/evaluate?studentId=${s.id}`}
                      className="btn btn-outline btn-sm"
                      style={{ fontSize: '0.75rem' }}
                    >
                      Evaluate
                    </a>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>

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
