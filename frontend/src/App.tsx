import { BrowserRouter, NavLink, Route, Routes } from 'react-router-dom'

function App() {
  return (
    <BrowserRouter>
      <div className="min-h-screen bg-canvas text-ink">
        <header className="flex items-center justify-between border-b border-line px-6 py-4">
          <span className="text-lg font-medium">Leja</span>
          <nav className="flex gap-4 text-sm">
            <NavLink to="/" className={({ isActive }) => (isActive ? 'text-accent' : 'text-muted')}>
              Dashboard
            </NavLink>
            <NavLink to="/journal" className={({ isActive }) => (isActive ? 'text-accent' : 'text-muted')}>
              Journal
            </NavLink>
            <NavLink to="/calendar" className={({ isActive }) => (isActive ? 'text-accent' : 'text-muted')}>
              Calendar
            </NavLink>
          </nav>
        </header>
        <main className="p-6">
          <Routes>
            <Route path="/" element={<h1 className="text-2xl">Dashboard</h1>} />
            <Route path="/journal" element={<h1 className="text-2xl">Journal</h1>} />
            <Route path="/calendar" element={<h1 className="text-2xl">Calendar</h1>} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  )
}

export default App