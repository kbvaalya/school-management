import React, { useState, useEffect } from 'react';
import { useAuth } from '../AuthContext';

export default function AdminDashboard() {
  const { fetchWithAuth } = useAuth();
  const [users, setUsers] = useState([]);
  const [classes, setClasses] = useState([]);

  // Form states
  const [newUser, setNewUser] = useState({ fullName: '', email: '', role: 'ROLE_USER' });
  const [newClass, setNewClass] = useState({ name: '' });

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const u = await fetchWithAuth('/api/admin/users');
      const c = await fetchWithAuth('/api/admin/classes');
      setUsers(u); setClasses(c);
    } catch (err) { alert(err.message); }
  };

  const assignManager = async (classId, managerId) => {
    if (!managerId) return;
    try {
      await fetchWithAuth(`/api/admin/classes/${classId}/manager/${managerId}`, { method: 'PATCH' });
      loadData();
    } catch (err) { alert(err.message); }
  };

  const enrollStudent = async (classId, studentId) => {
    if (!studentId) return;
    try {
      await fetchWithAuth(`/api/admin/classes/${classId}/students/${studentId}`, { method: 'POST' });
      loadData();
    } catch (err) { alert(err.message); }
  };

  const managers = users.filter(u => u.role === 'ROLE_MANAGER');
  const students = users.filter(u => u.role === 'ROLE_USER');

  const handleCreateUser = async (e) => {
    e.preventDefault();
    try {
      const res = await fetchWithAuth('/api/admin/users', { method: 'POST', body: JSON.stringify(newUser) });
      alert(`User Created! Username: ${res.username}, Password: ${res.generatedPassword}`);
      setNewUser({ fullName: '', email: '', role: 'ROLE_USER' });
      loadData();
    } catch (err) { alert(err.message); }
  };

  const handleCreateClass = async (e) => {
    e.preventDefault();
    try {
      await fetchWithAuth('/api/admin/classes', { method: 'POST', body: JSON.stringify(newClass) });
      setNewClass({ name: '' });
      loadData();
    } catch (err) { alert(err.message); }
  };

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl font-bold text-slate-900">Admin Dashboard</h1>
        <p className="text-slate-500 mt-2">Manage users and classes</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div className="card p-6">
          <h2 className="text-xl font-bold text-slate-800 mb-4">Create User</h2>
          <form className="space-y-4" onSubmit={handleCreateUser}>
            <input placeholder="Full Name" required value={newUser.fullName} onChange={e => setNewUser({...newUser, fullName: e.target.value})} className="w-full px-3 py-2 border rounded-lg" />
            <input type="email" placeholder="Email" required value={newUser.email} onChange={e => setNewUser({...newUser, email: e.target.value})} className="w-full px-3 py-2 border rounded-lg" />
            <select value={newUser.role} onChange={e => setNewUser({...newUser, role: e.target.value})} className="w-full px-3 py-2 border rounded-lg">
              <option value="ROLE_USER">Student</option>
              <option value="ROLE_MANAGER">Manager</option>
            </select>
            <button className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700">Create User</button>
          </form>
        </div>

        <div className="card p-6">
          <h2 className="text-xl font-bold text-slate-800 mb-4">Create Class</h2>
          <form className="space-y-4" onSubmit={handleCreateClass}>
            <input placeholder="Class Name (e.g. 10-A)" required value={newClass.name} onChange={e => setNewClass({name: e.target.value})} className="w-full px-3 py-2 border rounded-lg" />
            <button className="w-full bg-green-600 text-white py-2 rounded-lg hover:bg-green-700">Create Class</button>
          </form>
        </div>
      </div>

      <div className="card p-6">
        <h2 className="text-xl font-bold text-slate-800 mb-4">All Classes</h2>
        <div className="divide-y space-y-4">
          {classes.map(c => (
            <div key={c.id} className="pt-4 pb-2 flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
              <div>
                <p className="font-bold text-lg">{c.name}</p>
                <p className="text-sm text-slate-500">
                  Manager: {c.manager ? c.manager.fullName : 'None'} | Students: {c.studentCount}
                </p>
              </div>
              <div className="flex gap-4">
                <select onChange={(e) => assignManager(c.id, e.target.value)} defaultValue="" className="px-2 py-1 border rounded text-sm bg-slate-50">
                  <option value="" disabled>Assign Manager...</option>
                  {managers.map(m => <option key={m.id} value={m.id}>{m.fullName}</option>)}
                </select>
                <select onChange={(e) => { enrollStudent(c.id, e.target.value); e.target.value = ''; }} defaultValue="" className="px-2 py-1 border rounded text-sm bg-slate-50">
                  <option value="" disabled>Enroll Student...</option>
                  {students.map(s => <option key={s.id} value={s.id}>{s.fullName}</option>)}
                </select>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
