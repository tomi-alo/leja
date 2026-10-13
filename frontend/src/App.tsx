import { BrowserRouter, NavLink, Route, Routes, useLocation } from 'react-router-dom'
import Calendar from './Calendar'
import Dashboard from './Dashboard'
import Journal from './Journal'
import TradeDetail from './TradeDetail'
import ThemeToggle from './ThemeToggle'

const linkClass = ({ isActive }: { isActive: boolean }) =>
  `rounded-full px-3 py-1.5 transition-colors duration-300 ${
    isActive ? 'bg-accent/15 text-accent' : 'text-muted hover:text-ink'
  }`

function Shell() {
  const location = useLocation()

  return (
    <div className="min-h-screen bg-canvas text-ink transition-colors duration-300">
      <header className="sticky top-0 z-10 border-b border-line bg-canvas/80 backdrop-blur transition-colors duration-300">
        <div className="mx-auto flex max-w-6xl items-center justify-between gap-4 px-6 py-4">
          <span className="text-lg font-medium tracking-tight"><strong>LEJA</strong></span>
          <div className="flex items-center gap-4">
            <nav className="flex gap-1 text-sm">
              <NavLink to="/" className={linkClass} end>
                Dashboard
              </NavLink>
              <NavLink to="/journal" className={linkClass} end>
                Journal
              </NavLink>
              <NavLink to="/calendar" className={linkClass}>
                Calendar
              </NavLink>
            </nav>
            <ThemeToggle />
          </div>
        </div>
      </header>
      <main className="mx-auto max-w-6xl px-6 py-8">
        <div key={location.pathname} className="animate-rise">
          <Routes location={location}>
            <Route path="/" element={<Dashboard />} />
            <Route path="/journal/:id" element={<TradeDetail />} />
            <Route path="/journal" element={<Journal />} />
            <Route path="/calendar" element={<Calendar />} />
          </Routes>
        </div>
      </main>
    </div>
  )
}

function App() {
  return (
    <BrowserRouter>
      <Shell />
    </BrowserRouter>
  )
}

export default App
