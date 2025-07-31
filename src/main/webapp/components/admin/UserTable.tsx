// src/main/webapp/components/admin/UserTable.tsx
import React from 'react';
import { UserInfo } from '../../types/admin';

interface UserTableProps {
  users: UserInfo[];
  onUpdate: (user: UserInfo) => void;
  onDelete: (userId: number) => void;
  onStatusChange: (userId: number, newStatus: boolean) => void;
}

const UserTable: React.FC<UserTableProps> = ({ users, onUpdate, onDelete, onStatusChange }) => {
  return (
    <table className="table table-striped table-hover">
      <thead>
        <tr>
          <th>ID</th>
          <th>Username</th>
          <th>Email</th>
          <th>Status</th>
          <th>Enabled</th>
          <th>Roles</th>
          <th>Created At</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {users.length === 0 ? (
          <tr>
            <td colSpan={8} className="text-center">No users found.</td>
          </tr>
        ) : (
          users.map(user => (
            <tr key={user.id}>
              <td>{user.id}</td>
              <td>{user.username}</td>
              <td>{user.email}</td>
              <td>
                <span className={`badge ${user.status === 'ACTIVE' ? 'bg-success' : 'bg-secondary'}`}>
                  {user.status}
                </span>
              </td>
              <td>
                <div className="form-check form-switch">
                  <input
                    className="form-check-input"
                    type="checkbox"
                    checked={user.enabled}
                    onChange={(e) => onStatusChange(user.id, e.target.checked)}
                    id={`switch-${user.id}`}
                  />
                  <label className="form-check-label" htmlFor={`switch-${user.id}`}>
                    {user.enabled ? 'Enabled' : 'Disabled'}
                  </label>
                </div>
              </td>
              <td>
                {user.roles.map(role => (
                  <span key={role} className="badge bg-info me-1">
                    {role}
                  </span>
                ))}
              </td>
              <td>{new Date(user.created_at).toLocaleDateString()}</td>
              <td>
                <button 
                  className="btn btn-sm btn-outline-primary me-2" 
                  onClick={() => onUpdate(user)}
                  aria-label={`Edit user ${user.username}`}
                >
                  <i className="bi bi-pencil"></i> Edit
                </button>
                <button 
                  className="btn btn-sm btn-outline-danger" 
                  onClick={() => onDelete(user.id)}
                  aria-label={`Delete user ${user.username}`}
                >
                  <i className="bi bi-trash"></i> Delete
                </button>
              </td>
            </tr>
          ))
        )}
      </tbody>
    </table>
  );
};

export default UserTable;
