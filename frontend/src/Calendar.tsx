import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { money, pnlByDay, pnlColor, todayKey } from './format'
import { useTrades } from './useTrades'

const weekdays = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
const monthNames = Array.from({ length: 12 }, (_, index) =>
  new Date(2000, index, 1).toLocaleString(undefined, { month: 'short' }),
)

function Calendar() {
  const { trades, loading, error } = useTrades()
  const [cursor, setCursor] = useState(() => new Date())
  const [pickerOpen, setPickerOpen] = useState(false)
  const [pickerMode, setPickerMode] = useState<'month' | 'year'>('month')
  const [pickerYear, setPickerYear] = useState(() => new Date().getFullYear())
  const [yearDraft, setYearDraft] = useState(() => String(new Date().getFullYear()))
  const pickerRef = useRef<HTMLDivElement>(null)

  function goToYear(next: number) {
    const maxYear = new Date().getFullYear()
    const clamped = Math.min(Math.max(next, 1900), maxYear)
    setPickerYear(clamped)
    setYearDraft(String(clamped))
  }

  useEffect(() => {
    if (!pickerOpen) return

    function onPointerDown(event: PointerEvent) {
      if (!pickerRef.current?.contains(event.target as Node)) {
        setPickerOpen(false)
      }
    }

    document.addEventListener('pointerdown', onPointerDown)
    return () => document.removeEventListener('pointerdown', onPointerDown)
  }, [pickerOpen])

  if (loading) return <p className="text-muted">Loading trades...</p>
  if (error) return <p className="text-loss">{error}</p>

  const now = new Date()
  const currentYear = now.getFullYear()
  const currentMonth = now.getMonth()
  const year = cursor.getFullYear()
  const month = cursor.getMonth()
  const atCurrentMonth = year === currentYear && month === currentMonth
  const firstWeekday = (new Date(year, month, 1).getDay() + 6) % 7
  const daysInMonth = new Date(year, month + 1, 0).getDate()
  const totals = pnlByDay(trades)
  const today = todayKey()

  const cells: Array<number | null> = [
    ...Array.from({ length: firstWeekday }, () => null),
    ...Array.from({ length: daysInMonth }, (_, index) => index + 1),
  ]

  function isFutureMonth(nextYear: number, nextMonth: number) {
    return nextYear > currentYear || (nextYear === currentYear && nextMonth > currentMonth)
  }

  function shiftMonth(amount: number) {
    const next = new Date(year, month + amount, 1)
    if (isFutureMonth(next.getFullYear(), next.getMonth())) return
    setCursor(next)
  }

  function selectMonth(nextYear: number, nextMonth: number) {
    if (isFutureMonth(nextYear, nextMonth)) return
    setCursor(new Date(nextYear, nextMonth, 1))
    setPickerOpen(false)
  }

  function togglePicker() {
    setPickerYear(year)
    setYearDraft(String(year))
    setPickerMode('month')
    setPickerOpen((open) => !open)
  }

  function yearWindowStart(value: number) {
    return value - (value % 12)
  }

  return (
    <div className="flex flex-col gap-4">
      <div className="flex items-center justify-between">
        <div className="relative" ref={pickerRef}>
          <button
            type="button"
            onClick={togglePicker}
            aria-expanded={pickerOpen}
            aria-haspopup="dialog"
            className="rounded-lg text-left text-2xl font-medium transition-colors duration-300 hover:text-accent"
          >
            {cursor.toLocaleString(undefined, { month: 'long', year: 'numeric' })}
          </button>
          {pickerOpen && (
            <div className="absolute top-full left-0 z-20 mt-2 w-64 rounded-2xl border border-line bg-panel p-3 shadow-sm">
              {pickerMode === 'month' ? (
                <>
                  <div className="mb-3 flex items-center justify-between">
                    <button
                      type="button"
                      onClick={() => goToYear(pickerYear - 1)}
                      className="rounded-full border border-line px-2 py-1 text-sm transition-colors duration-300 hover:border-accent"
                    >
                      Previous
                    </button>
                    <input
                      aria-label="Year"
                      inputMode="numeric"
                      maxLength={4}
                      value={yearDraft}
                      onChange={(event) => {
                        const digits = event.target.value.replace(/\D/g, '').slice(0, 4)
                        setYearDraft(digits)
                        if (digits.length === 4) goToYear(Number(digits))
                      }}
                      onBlur={() => {
                        if (yearDraft.length === 4) goToYear(Number(yearDraft))
                        else setYearDraft(String(pickerYear))
                      }}
                      onKeyDown={(event) => {
                        if (event.key === 'Enter') {
                          event.preventDefault()
                          if (yearDraft.length === 4) goToYear(Number(yearDraft))
                          else setYearDraft(String(pickerYear))
                        }
                        if (event.key === 'ArrowDown') {
                          event.preventDefault()
                          setPickerMode('year')
                        }
                      }}
                      className="w-16 rounded-lg border border-line bg-canvas px-1 py-1 text-center text-sm font-medium text-ink outline-none focus:border-accent"
                    />
                    <button
                      type="button"
                      disabled={pickerYear >= currentYear}
                      onClick={() => goToYear(pickerYear + 1)}
                      className="rounded-full border border-line px-2 py-1 text-sm transition-colors duration-300 hover:border-accent disabled:cursor-not-allowed disabled:opacity-40 disabled:hover:border-line"
                    >
                      Next
                    </button>
                  </div>
                  <div className="grid grid-cols-3 gap-2">
                    {monthNames.map((name, index) => {
                      const disabled = isFutureMonth(pickerYear, index)
                      const selected = pickerYear === year && index === month
                      return (
                        <button
                          key={name}
                          type="button"
                          disabled={disabled}
                          onClick={() => selectMonth(pickerYear, index)}
                          className={`rounded-lg px-2 py-2 text-sm transition-colors duration-300 ${
                            selected ? 'bg-accent/15 text-accent' : 'text-ink hover:bg-accent/5'
                          } disabled:cursor-not-allowed disabled:text-muted disabled:opacity-40 disabled:hover:bg-transparent`}
                        >
                          {name}
                        </button>
                      )
                    })}
                  </div>
                </>
              ) : (
                <>
                  <div className="mb-3 flex items-center justify-between">
                    <button
                      type="button"
                      onClick={() => goToYear(pickerYear - 12)}
                      className="rounded-full border border-line px-2 py-1 text-sm transition-colors duration-300 hover:border-accent"
                    >
                      Previous
                    </button>
                    <span className="text-sm font-medium">
                      {yearWindowStart(pickerYear)}–{yearWindowStart(pickerYear) + 11}
                    </span>
                    <button
                      type="button"
                      disabled={yearWindowStart(pickerYear) + 12 > currentYear}
                      onClick={() => goToYear(pickerYear + 12)}
                      className="rounded-full border border-line px-2 py-1 text-sm transition-colors duration-300 hover:border-accent disabled:cursor-not-allowed disabled:opacity-40 disabled:hover:border-line"
                    >
                      Next
                    </button>
                  </div>
                  <div className="grid grid-cols-3 gap-2">
                    {Array.from({ length: 12 }, (_, index) => yearWindowStart(pickerYear) + index).map((nextYear) => {
                      const disabled = nextYear > currentYear
                      const selected = nextYear === year
                      return (
                        <button
                          key={nextYear}
                          type="button"
                          disabled={disabled}
                          onClick={() => {
                            goToYear(nextYear)
                            setPickerMode('month')
                          }}
                          className={`rounded-lg px-2 py-2 text-sm transition-colors duration-300 ${
                            selected ? 'bg-accent/15 text-accent' : 'text-ink hover:bg-accent/5'
                          } disabled:cursor-not-allowed disabled:text-muted disabled:opacity-40 disabled:hover:bg-transparent`}
                        >
                          {nextYear}
                        </button>
                      )
                    })}
                  </div>
                </>
              )}
            </div>
          )}
        </div>
        <div className="flex gap-2">
          <button type="button" onClick={() => shiftMonth(-1)} className="rounded-full border border-line bg-panel px-3 py-2 text-sm transition-colors duration-300 hover:border-accent">
            Previous
          </button>
          <button
            type="button"
            disabled={atCurrentMonth}
            onClick={() => shiftMonth(1)}
            className="rounded-full border border-line bg-panel px-3 py-2 text-sm transition-colors duration-300 hover:border-accent disabled:cursor-not-allowed disabled:opacity-40 disabled:hover:border-line"
          >
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
          const future = key > today
          const colored = summary === undefined ? 'bg-panel' : summary.pnl > 0 ? 'bg-win/15' : summary.pnl < 0 ? 'bg-loss/15' : 'bg-panel'
          const cellClass = `flex min-h-24 flex-col rounded-xl border p-2 text-left shadow-sm ${colored} ${key === today ? 'border-accent' : 'border-line'}`

          if (future) {
            return (
              <div key={key} className={`${cellClass} cursor-default opacity-40`}>
                <div className="text-muted">{day}</div>
              </div>
            )
          }

          return (
            <Link
              key={key}
              to={`/journal?date=${key}`}
              className={`${cellClass} transition-all duration-300 hover:-translate-y-0.5 hover:border-accent`}
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
