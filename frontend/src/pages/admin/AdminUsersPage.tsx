import { useState, useEffect, type FormEvent } from 'react';
import api from '../../services/api';
import type { User, Role, Page } from '../../types';

export default function AdminUsersPage() {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Form state
  const [newUsername, setNewUsername] = useState('');
  const [newEmail, setNewEmail] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [newFullName, setNewFullName] = useState('');
  const [newRole, setNewRole] = useState<Role>('PLACEMENT_OFFICER');

  const fetchUsers = () => {
    setLoading(true);
    const queryParams = new URLSearchParams({
      page: page.toString(),
      size: '10',
    });
    if (search) queryParams.set('query', search);
    if (roleFilter) queryParams.set('role', roleFilter);

    api.get<Page<User>>(`/api/admin/users?${queryParams.toString()}`)
      .then((res) => {
        setUsers(res.content || []);
        setTotalPages(res.totalPages || 1);
      })
      .catch((err: unknown) => {
        const errorObj = err as { message?: string };
        setError(errorObj?.message || 'Failed to fetch users');
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchUsers();
  }, [page, roleFilter]);

  const handleSearch = (e: FormEvent) => {
    e.preventDefault();
    setPage(0);
    fetchUsers();
  };

  const handleToggleStatus = async (user: User) => {
    try {
      await api.put(`/api/admin/users/${user.id}/status`, { active: !user.isActive });
      fetchUsers();
    } catch (err: unknown) {
      const errorObj = err as { message?: string };
      alert(errorObj?.message || 'Failed to update user status');
    }
  };

  const handleCreateUser = async (e: FormEvent) => {
    e.preventDefault();
    try {
      await api.post('/api/admin/users', {
        username: newUsername,
        email: newEmail,
        password: newPassword,
        fullName: newFullName,
        role: newRole,
      });
      setShowCreateModal(false);
      // Reset form
      setNewUsername('');
      setNewEmail('');
      setNewPassword('');
      setNewFullName('');
      fetchUsers();
    } catch (err: unknown) {
      const errorObj = err as { message?: string };
      alert(errorObj?.message || 'Failed to create user');
    }
  };

  return (
    <div>
      <div className="breadcrumb">
        <span>Administration</span>
        <span className="breadcrumb-separator">/</span>
        <span>User Management</span>
      </div>

      <div className="page-header">
        <div>
          <h1 className="page-title">User Accounts & Roles</h1>
          <p className="page-subtitle">Provision staff identities, assign roles, and manage account statuses.</p>
        </div>
        <button
          type="button"
          className="btn btn-primary btn-sm"
          onClick={() => setShowCreateModal(true)}
        >
          + Add New User
        </button>
      </div>

      {error && (
        <div className="login-error-alert" style={{ marginBottom: 'var(--space-4)' }}>
          {error}
        </div>
      )}

      {/* ── Filters & Search ── */}
      <div className="card" style={{ padding: 'var(--space-4)', marginBottom: 'var(--space-6)' }}>
        <form onSubmit={handleSearch} style={{ display: 'flex', gap: 'var(--space-4)', flexWrap: 'wrap', alignItems: 'center' }}>
          <div className="search-bar" style={{ maxWidth: '300px' }}>
            <span className="search-bar-icon">🔍</span>
            <input
              type="text"
              placeholder="Search by name, username, or email..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>

          <select
            className="form-select"
            value={roleFilter}
            onChange={(e) => { setRoleFilter(e.target.value); setPage(0); }}
            style={{ width: '200px' }}
          >
            <option value="">All Roles</option>
            <option value="ADMIN">Administrator</option>
            <option value="PLACEMENT_OFFICER">Placement Officer</option>
            <option value="STUDENT">Student</option>
          </select>

          <button type="submit" className="btn btn-outline btn-sm">
            Filter
          </button>
        </form>
      </div>

      {/* ── Users Table ── */}
      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <div style={{ overflowX: 'auto' }}>
          <table className="eqel-table">
            <thead>
              <tr>
                <th>User Details</th>
                <th>Role</th>
                <th>Status</th>
                <th>Created</th>
                <th style={{ textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={5} style={{ textAlign: 'center', padding: 'var(--space-8)' }}>
                    Loading user records...
                  </td>
                </tr>
              ) : users.length === 0 ? (
                <tr>
                  <td colSpan={5} style={{ textAlign: 'center', padding: 'var(--space-8)', color: 'var(--ink-3)' }}>
                    No users matching criteria.
                  </td>
                </tr>
              ) : (
                users.map((u) => (
                  <tr key={u.id}>
                    <td>
                      <div style={{ fontWeight: 600, color: 'var(--ink)' }}>{u.fullName}</div>
                      <div style={{ fontSize: '0.8125rem', color: 'var(--ink-3)', fontFamily: 'var(--font-mono)' }}>
                        @{u.username} · {u.email}
                      </div>
                    </td>
                    <td>
                      <span className={`badge ${u.role === 'ADMIN' ? 'badge-pending' : u.role === 'PLACEMENT_OFFICER' ? 'badge-verified' : 'badge-eligible'}`}>
                        {u.role}
                      </span>
                    </td>
                    <td>
                      <span className={`badge ${u.isActive ? 'badge-active' : 'badge-inactive'}`}>
                        {u.isActive ? 'Active' : 'Disabled'}
                      </span>
                    </td>
                    <td style={{ fontSize: '0.8125rem', fontFamily: 'var(--font-mono)' }}>
                      {u.createdAt ? new Date(u.createdAt).toLocaleDateString() : 'N/A'}
                    </td>
                    <td style={{ textAlign: 'right' }}>
                      <button
                        type="button"
                        className={`btn btn-sm ${u.isActive ? 'btn-ghost' : 'btn-outline'}`}
                        onClick={() => handleToggleStatus(u)}
                        style={{ fontSize: '0.75rem' }}
                      >
                        {u.isActive ? 'Deactivate' : 'Activate'}
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        {/* ── Pagination ── */}
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

      {/* ── Create User Modal ── */}
      {showCreateModal && (
        <div className="modal-backdrop" onClick={() => setShowCreateModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h2 className="modal-title">Provision New Account</h2>
            <form onSubmit={handleCreateUser} className="login-form">
              <div className="form-group">
                <label className="form-label">Full Name</label>
                <input
                  type="text"
                  className="form-input"
                  required
                  value={newFullName}
                  onChange={(e) => setNewFullName(e.target.value)}
                  placeholder="e.g. Dr. Rajesh Sharma"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Username</label>
                <input
                  type="text"
                  className="form-input"
                  required
                  value={newUsername}
                  onChange={(e) => setNewUsername(e.target.value)}
                  placeholder="e.g. rsharma"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Institutional Email</label>
                <input
                  type="email"
                  className="form-input"
                  required
                  value={newEmail}
                  onChange={(e) => setNewEmail(e.target.value)}
                  placeholder="e.g. rsharma@university.edu"
                />
              </div>

              <div className="form-group">
                <label className="form-label">Temporary Password</label>
                <input
                  type="password"
                  className="form-input"
                  required
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  placeholder="Strong password"
                />
              </div>

              <div className="form-group">
                <label className="form-label">System Role</label>
                <select
                  className="form-select"
                  value={newRole}
                  onChange={(e) => setNewRole(e.target.value as Role)}
                >
                  <option value="PLACEMENT_OFFICER">Placement Officer</option>
                  <option value="STUDENT">Student</option>
                  <option value="ADMIN">Administrator</option>
                </select>
              </div>

              <div className="modal-actions">
                <button
                  type="button"
                  className="btn btn-ghost"
                  onClick={() => setShowCreateModal(false)}
                >
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Save & Provision
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
