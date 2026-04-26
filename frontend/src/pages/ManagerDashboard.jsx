import React, { useState, useEffect } from 'react';
import { useAuth } from '../AuthContext';
import { format } from 'date-fns';

export default function ManagerDashboard() {
  const { fetchWithAuth } = useAuth();
  const [classes, setClasses] = useState([]);
  const [selectedClass, setSelectedClass] = useState('');
  const [students, setStudents] = useState([]);
  const [date, setDate] = useState(format(new Date(), 'yyyy-MM-dd'));

  useEffect(() => {
    fetchWithAuth('/api/manager/classes').then(setClasses).catch(e => alert(e.message));
  }, []);

  useEffect(() => {
    if (selectedClass) {
      fetchWithAuth(`/api/manager/classes/${selectedClass}`).then(res => {
        setStudents(res.students || []);
      }).catch(e => alert(e.message));
    } else {
      setStudents([]);
    }
  }, [selectedClass]);

  const markAttendance = async (studentId, status) => {
    try {
      await fetchWithAuth('/api/attendance', {
        method: 'POST',
        body: JSON.stringify({
          classId: parseInt(selectedClass),
          studentId,
          date,
          status
        })
      });
      alert('Attendance marked');
    } catch (err) { alert(err.message); }
  };

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl font-bold text-slate-900">Attendance Journal</h1>
        <p className="text-slate-500 mt-2">Manage attendance for your classes</p>
      </div>

      <div className="card p-6">
        <div className="flex gap-4 mb-6">
          <div className="flex-1">
            <label className="block text-sm font-medium mb-1">Class</label>
            <select value={selectedClass} onChange={e => setSelectedClass(e.target.value)} className="w-full border rounded-lg p-2">
              <option value="">Select a class...</option>
              {classes.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
            </select>
          </div>
          <div className="w-48">
            <label className="block text-sm font-medium mb-1">Date</label>
            <input type="date" value={date} onChange={e => setDate(e.target.value)} className="w-full border rounded-lg p-2" />
          </div>
        </div>

        {selectedClass && (
          <div>
            <h3 className="font-bold mb-4">Students</h3>
            <div className="space-y-2">
              {students.map(s => (
                <div key={s.id} className="flex justify-between items-center p-3 border rounded-lg">
                  <span>{s.fullName}</span>
                  <div className="flex gap-2">
                    <button onClick={() => markAttendance(s.id, 'PRESENT')} className="px-4 py-1 bg-green-100 text-green-700 rounded hover:bg-green-200">Present</button>
                    <button onClick={() => markAttendance(s.id, 'ABSENT')} className="px-4 py-1 bg-red-100 text-red-700 rounded hover:bg-red-200">Absent</button>
                  </div>
                </div>
              ))}
              {students.length === 0 && <p className="text-slate-500">No students in this class.</p>}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
