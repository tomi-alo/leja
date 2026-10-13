import { useState } from 'react'
import { Link } from 'react-router-dom'
import { dayKey, money, pnlByDay, pnlColor } from './format'
import { useTrades } from './useTrades'

const weekdays = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']

function Calendar() {
  const { trades, loading, error } = useTrades()
  const [cursor, setCursor] = useState(() => new Date())

  if (loading) return <p className="text-muted">Loading trades...</p>
  if (error) return <p className="text-loss">{error}</p>

  const year = cursor.getFullYear()
  const month = cursor.getMonth()
  const firstWeekday = (new Date(year, month, 1).getDay() + 6) % 7
  const daysInMonth = new Date(year, month + 1, 0).getDate()
  const totals = pnlByDay(trades)
  const today = dayKey(new Date().toISOString())

  const cells: Array<number | null> = [
    ...Array.from({ length: firstWeekday }, () => null),
    ...Array.from({ length: daysInMonth }, (_, index) => index + 1),
  ]

  function shiftMonth(amount: number) {
    setCursor(new Date(year, month + amount, 1))
  }

  return (
    <div className="flex flex-col gap-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-medium">
          {cursor.toLocaleString(undefined, { month: 'long', year: 'numeric' })}
        </h1>
        <div className="flex gap-2">
          <button type="button" onClick={() => shiftMonth(-1)} className="rounded-full border border-line bg-panel px-3 py-2 text-sm transition-colors duration-300 hover:border-accent">
            Previous
          </button>
          <button type="button" onClick={() => shiftMonth(1)} className="rounded-full border border-line bg-panel px-3 py-2 text-sm transition-colors duration-300 hover:border-accent">
            Next
          </button>
        </div>
      </div>
      <div key={`${year}-${month}`} className="animate-rise grid grid-cols-7 gap-2 text-center text-sm">
        {weekdays.map((day) => (
          <div key={day} className="text-muted">
            {day}
          </div>
        ))}
        {cells.map((day, index) => {
          if (day === null) return <div key={`pad-${index}`} />

          const key = `${year}-${String(month + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`
          const summary = totals.get(key)
          const colored = summary === undefined ? 'bg-panel' : summary.pnl > 0 ? 'bg-win/15' : summary.pnl < 0 ? 'bg-loss/15' : 'bg-panel'

          return (
            <Link
              key={key}
              to={`/journal?date=${key}`}
              className={`flex min-h-24 flex-col rounded-xl border p-2 text-left shadow-sm transition-all duration-300 hover:-translate-y-0.5 hover:border-accent ${colored} ${key === today ? 'border-accent' : 'border-line'}`}
            >
              <div className="text-muted">{day}</div>
              {summary && (
                <>
                  <div className={`mt-2 text-xs font-medium ${pnlColor(summary.pnl)}`}>{money(summary.pnl)}</div>
                  <div className="text-xs text-muted">{summary.count} {summary.count === 1 ? 'trade' : 'trades'}</div>
                </>
              )}
            </Link>
          )
        })}
      </div>
    </div>
  )
}

export default Calendar
