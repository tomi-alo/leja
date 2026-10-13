import { useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { createTrade, deleteTrade, updateTrade } from './api'
import { dayKey, dayLabel, money, pnlColor, price, ratio, todayKey, when } from './format'
import TradeForm from './TradeForm'
import type { Credentials, Trade, TradeWrite } from './types'
import { useTrades } from './useTrades'

function Journal() {
  const { trades, loading, error, reload } = useTrades()
  const navigate = useNavigate()
  const [params] = useSearchParams()
  const date = params.get('date')
  const [formOpen, setFormOpen] = useState(false)
  const [editing, setEditing] = useState<Trade | null>(null)
  const [username, setUsername] = useState('leja')
  const [password, setPassword] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [actionError, setActionError] = useState<string | null>(null)
  const [symbol, setSymbol] = useState('ALL')
  const [session, setSession] = useState('ALL')
  const [outcome, setOutcome] = useState('ALL')

  const dated = date ? trades.filter((trade) => dayKey(trade.executedAt) === date) : trades
  const symbols = [...new Set(dated.map((trade) => trade.symbol))].sort()
  const activeSymbol = symbol !== 'ALL' && symbols.includes(symbol) ? symbol : 'ALL'
  const visible = dated.filter((trade) => {
    if (activeSymbol !== 'ALL' && trade.symbol !== activeSymbol) return false
    if (session !== 'ALL' && trade.session !== session) return false
    if (outcome !== 'ALL' && trade.outcome !== outcome) return false
    return true
  })

  function credentials(): Credentials {
    return { username, password }
  }

  function openNew() {
    setEditing(null)
    setFormOpen(true)
    setActionError(null)
  }

  function openEdit(trade: Trade) {
    setPassword('')
    setEditing(trade)
    setFormOpen(true)
    setActionError(null)
  }

  async function save(draft: TradeWrite) {
    if (!password) {
      throw new Error('Enter the API password before saving')
    }

    setSubmitting(true)
    try {
      if (editing) {
        await updateTrade(editing.id, draft, credentials())
      } else {
        await createTrade(draft, credentials())
      }
      setFormOpen(false)
      setEditing(null)
      setPassword('')
      reload()
    } finally {
      setSubmitting(false)
    }
  }

  async function remove(trade: Trade) {
    setActionError(null)
    if (!password) {
      setActionError('Enter the API password before deleting')
      return
    }
    if (!window.confirm(`Delete ${trade.symbol} trade ${trade.id}?`)) return

    try {
      await deleteTrade(trade.id, credentials())
      setPassword('')
      reload()
    } catch (caught) {
      setActionError(caught instanceof Error ? caught.message : 'Could not delete trade')
    }
  }

  if (loading) return <p className="text-muted">Loading trades...</p>
  if (error) return <p className="text-loss">{error}</p>

  return (
    <div className="flex flex-col gap-4">
      <div className="flex items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-medium">{date ? dayLabel(date) : 'Journal'}</h1>
          {date && (
            <Link to="/journal" className="text-sm text-accent">
              All trades
            </Link>
          )}
        </div>
        <button type="button" onClick={openNew} className="rounded-full bg-accent px-4 py-2 text-sm font-medium text-on-accent transition-transform duration-300 hover:-translate-y-0.5">
          Add trade
        </button>
      </div>

      <div className="flex flex-wrap items-end gap-3 rounded-2xl border border-line bg-panel p-4 shadow-sm transition-colors duration-300">
        <label className="flex flex-col gap-1 text-sm text-muted">
          Username
          <input className="rounded-lg border border-line bg-canvas px-3 py-2 text-ink transition-colors duration-300" value={username} onChange={(event) => setUsername(event.target.value)} />
        </label>
        <label className="flex flex-col gap-1 text-sm text-muted">
          API password
          <input className="rounded-lg border border-line bg-canvas px-3 py-2 text-ink transition-colors duration-300" type="password" value={password} onChange={(event) => setPassword(event.target.value)} />
        </label>
        <p className="pb-2 text-sm text-muted">Used only for save and delete.</p>
        <label className="flex flex-col gap-1 text-sm text-muted">
          Symbol
          <select className="rounded-lg border border-line bg-canvas px-3 py-2 text-ink transition-colors duration-300" value={activeSymbol} onChange={(event) => setSymbol(event.target.value)}>
            <option value="ALL">All</option>
            {symbols.map((name) => (
              <option key={name} value={name}>
                {name}
              </option>
            ))}
          </select>
        </label>
        <label className="flex flex-col gap-1 text-sm text-muted">
          Session
          <select className="rounded-lg border border-line bg-canvas px-3 py-2 text-ink transition-colors duration-300" value={session} onChange={(event) => setSession(event.target.value)}>
            <option value="ALL">All</option>
            <option value="NY">NY</option>
            <option value="ASIA">ASIA</option>
            <option value="LONDON">LONDON</option>
          </select>
        </label>
        <label className="flex flex-col gap-1 text-sm text-muted">
          Outcome
          <select className="rounded-lg border border-line bg-canvas px-3 py-2 text-ink transition-colors duration-300" value={outcome} onChange={(event) => setOutcome(event.target.value)}>
            <option value="ALL">All</option>
            <option value="WIN">Win</option>
            <option value="LOSS">Loss</option>
            <option value="BREAK EVEN">Break even</option>
          </select>
        </label>
      </div>

      {actionError && <p className="text-sm text-loss">{actionError}</p>}

      {formOpen && (
        <TradeForm
          key={editing?.id ?? date ?? 'new'}
          trade={editing}
          defaultDate={date && date <= todayKey() ? date : null}
          submitting={submitting}
          onSubmit={save}
          onCancel={() => setFormOpen(false)}
        />
      )}

      <div className="overflow-x-auto rounded-2xl border border-line bg-panel shadow-sm transition-colors duration-300">
        <table className="w-full text-left text-sm">
          <thead className="text-muted">
            <tr>
              <th className="px-3 py-2 font-medium">Symbol</th>
              <th className="px-3 py-2 font-medium">Side</th>
              <th className="px-3 py-2 font-medium">Session</th>
              <th className="px-3 py-2 text-right font-medium">Size</th>
              <th className="px-3 py-2 text-right font-medium">Entry</th>
              <th className="px-3 py-2 text-right font-medium">Exit</th>
              <th className="px-3 py-2 text-right font-medium">PnL</th>
              <th className="px-3 py-2 text-right font-medium">R:R</th>
              <th className="px-3 py-2 font-medium">Outcome</th>
              <th className="px-3 py-2 font-medium">When</th>
              <th className="px-3 py-2 font-medium"></th>
            </tr>
          </thead>
          <tbody>
            {visible.map((trade) => (
              <tr
                key={trade.id}
                onClick={() => navigate(`/journal/${trade.id}`)}
                className="cursor-pointer border-t border-line transition-colors duration-300 hover:bg-accent/5"
              >
                <td className="px-3 py-2">{trade.symbol}</td>
                <td className="px-3 py-2">{trade.direction}</td>
                <td className="px-3 py-2">{trade.session}</td>
                <td className="px-3 py-2 text-right tabular-nums">{trade.positionSize.toFixed(2)}</td>
                <td className="px-3 py-2 text-right">{price(trade.entryPrice)}</td>
                <td className="px-3 py-2 text-right">{price(trade.exitPrice)}</td>
                <td className={`px-3 py-2 text-right ${pnlColor(trade.pnl)}`}>{money(trade.pnl)}</td>
                <td className="px-3 py-2 text-right">{ratio(trade.riskToRewardRatio)}</td>
                <td className="px-3 py-2">{trade.outcome}</td>
                <td className="px-3 py-2 text-muted">{when(trade.executedAt)}</td>
                <td className="px-3 py-2" onClick={(event) => event.stopPropagation()}>
                  <button type="button" onClick={() => openEdit(trade)} className="mr-3 text-accent">
                    Edit
                  </button>
                  <button type="button" onClick={() => remove(trade)} className="text-loss">
                    Delete
                  </button>
                </td>
              </tr>
            ))}
            {visible.length === 0 && (
              <tr>
                <td colSpan={11} className="px-3 py-4 text-muted">
                  {date ? `No trades on ${dayLabel(date)}.` : symbol !== 'ALL' || session !== 'ALL' || outcome !== 'ALL' ? 'No trades match these filters.' : 'No trades yet.'}
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  )
}

export default Journal
