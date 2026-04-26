import React, { useState, useEffect } from 'react';
import { useAuth } from '../AuthContext';
import { DayPicker } from 'react-day-picker';
import 'react-day-picker/dist/style.css';
import { format, parseISO } from 'date-fns';

export default function StudentDashboard() {
  const { fetchWithAuth } = useAuth();
  const [attendance, setAttendance] = useState([]);

  useEffect(() => {
    fetchWithAuth('/api/attendance').then(setAttendance).catch(e => alert(e.message));
  }, []);

  const presentDays = attendance.filter(a => a.status === 'PRESENT').map(a => parseISO(a.date));
  const absentDays = attendance.filter(a => a.status === 'ABSENT').map(a => parseISO(a.date));

  const modifiers = {
    present: presentDays,
    absent: absentDays
  };

  const modifiersStyles = {
    present: { backgroundColor: '#22c55e', color: 'white' },
    absent: { backgroundColor: '#ef4444', color: 'white' }
  };

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl font-bold text-slate-900">My Calendar</h1>
        <p className="text-slate-500 mt-2">View your attendance records</p>
      </div>

      <div className="card p-6 flex justify-center">
        <DayPicker
          modifiers={modifiers}
          modifiersStyles={modifiersStyles}
          className="scale-145 origin-top"
        />
      </div>

      <div className="flex justify-center gap-6 mt-4">
        <div className="flex items-center gap-2">
          <div className="w-4 h-4 bg-green-500 rounded"></div>
          <span>Present</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-4 h-4 bg-red-500 rounded"></div>
          <span>Absent</span>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-4 h-4 bg-slate-100 rounded"></div>
          <span>No Data</span>
        </div>
      </div>
    </div>
  );
}
