import React from 'react'
import { Routes, Route, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from './AuthContext'
import { LogOut, LayoutDashboard, Users, BookOpen, CalendarDays } from 'lucide-react'

// Pages
import Login from './pages/Login'
import AdminDashboard from './pages/AdminDashboard'
import ManagerDashboard from './pages/ManagerDashboard'
import StudentDashboard from './pages/StudentDashboard'

const PrivateRoute = ({ role, children }) => {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" />;
  if (role && user.role !== role) return <Navigate to="/" />;
  return children;
};

const Layout = ({ children }) => {
  const { user, logout } = useAuth();
  const nav = useNavigate();
  return (
    <div className="min-h-screen bg-slate-50 flex">
      <aside className="w-64 bg-white border-r border-slate-200 flex flex-col">
        <div className="p-6">
          <h1 className="text-xl font-bold text-slate-800 flex items-center gap-2">
            <BookOpen className="text-blue-600" /> School MS
          </h1>
        </div>
        <nav className="flex-1 px-4 space-y-2">
          <div className="px-4 py-2 bg-blue-50 text-blue-700 rounded-lg flex items-center gap-3 font-medium cursor-pointer">
            <LayoutDashboard size={20} /> Dashboard
          </div>
        </nav>
        <div className="p-4 border-t border-slate-200">
          <div className="flex items-center gap-3 mb-4 px-2">
            <div className="w-10 h-10 rounded-full bg-blue-100 text-blue-700 flex items-center justify-center font-bold">
              {user.fullName[0]}
            </div>
            <div>
              <p className="text-sm font-medium text-slate-800">{user.fullName}</p>
              <p className="text-xs text-slate-500">{user.role.replace('ROLE_', '')}</p>
            </div>
          </div>
          <button onClick={() => { logout(); nav('/login') }} className="w-full flex items-center gap-2 px-4 py-2 text-slate-600 hover:bg-slate-100 rounded-lg">
            <LogOut size={18} /> Logout
          </button>
        </div>
      </aside>
      <main className="flex-1 p-8">
        {children}
      </main>
    </div>
  )
}

function App() {
  const { user } = useAuth();

  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={
        <PrivateRoute>
          <Layout>
            {user?.role === 'ROLE_ADMIN' && <AdminDashboard />}
            {user?.role === 'ROLE_MANAGER' && <ManagerDashboard />}
            {user?.role === 'ROLE_USER' && <StudentDashboard />}
          </Layout>
        </PrivateRoute>
      } />
    </Routes>
  )
}

export default App
