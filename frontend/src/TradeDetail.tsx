import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { deleteTrade, getTrade, updateTrade } from './api'
import { money, pnlColor, price, ratio, when } from './format'
import TradeForm from './TradeForm'
import type { Credentials, Trade, TradeWrite } from './types'

function TradeDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [trade, setTrade] = useState<Trade | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [username, setUsername] = useState('leja')
  const [password, setPassword] = useState('')
  const [editing, setEditing] = useState(false)
  const [submitting, setSubmitting] = useState(false)
  const [actionError, setActionError] = useState<string | null>(null)

  const tradeId = Number(id)
  const invalidId = !Number.isInteger(tradeId) || tradeId <= 0

  function load(idToLoad: number) {
    getTrade(idToLoad)
      .then(setTrade)
      .catch((caught) => setError(caught instanceof Error ? caught.message : 'Could not load trade'))
  }

  useEffect(() => {
    if (invalidId) return

    let cancelled = false
    getTrade(tradeId)
      .then((data) => {
        if (!cancelled) setTrade(data)
      })
      .catch((caught) => {
        if (!cancelled) setError(caught instanceof Error ? caught.message : 'Could not load trade')
      })

    return () => {
      cancelled = true
    }
  }, [tradeId, invalidId])

  function credentials(): Credentials {
    return { username, password }
  }

  async function save(draft: TradeWrite) {
    if (!trade) return
    if (!password) {
      throw new Error('Enter the API password before saving')
    }

    setSubmitting(true)
    try {
      await updateTrade(trade.id, draft, credentials())
      setEditing(false)
      setPassword('')
      setError(null)
      load(trade.id)
    } finally {
      setSubmitting(false)
    }
  }

  async function remove() {
    if (!trade) return
    setActionError(null)
    if (!password) {
      setActionError('Enter the API password before deleting')
      return
    }
    if (!window.confirm(`Delete ${trade.symbol} trade ${trade.id}?`)) return

    try {
      await deleteTrade(trade.id, credentials())
      navigate('/journal')
    } catch (caught) {
      setActionError(caught instanceof Error ? caught.message : 'Could not delete trade')
    }
  }

  if (invalidId || error) {
    return (
      <div className="flex flex-col gap-3">
        <p className="text-loss">{error ?? 'Trade not found'}</p>
        <Link to="/journal" className="text-sm text-accent">
          Back to journal
        </Link>
      </div>
    )
  }

  if (!trade) return <p className="text-muted">Loading trade...</p>

  const fields = [
    { label: 'Symbol', value: trade.symbol },
    { label: 'Direction', value: trade.direction },
    { label: 'Session', value: trade.session },
    { label: 'Outcome', value: trade.outcome },
    { label: 'Position size', value: trade.positionSize.toFixed(2) },
    { label: 'Contract size', value: String(trade.contractSize) },
    { label: 'Entry', value: price(trade.entryPrice) },
    { label: 'Exit', value: price(trade.exitPrice) },
    { label: 'Stop loss', value: price(trade.stopLoss) },
    { label: 'Take profit', value: price(trade.takeProfit) },
    { label: 'PnL', value: money(trade.pnl), color: pnlColor(trade.pnl) },
    { label: 'R:R', value: ratio(trade.riskToRewardRatio) },
    { label: 'When', value: when(trade.executedAt) },
    { label: 'Reason', value: trade.reason || 'None' },
  ]

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-start justify-between gap-4">
        <div>
          <Link to="/journal" className="text-sm text-accent">
            Back to journal
          </Link>
          <h1 className="mt-2 text-2xl font-medium">
            {trade.symbol} {trade.direction}
          </h1>
          <p className="text-sm text-muted">Trade {trade.id}</p>
        </div>
        <div className="flex gap-2">
          <button
            type="button"
            onClick={() => {
              setEditing((open) => {
                if (!open) setPassword('')
                return !open
              })
            }}
            className="rounded-full border border-line px-4 py-2 text-sm text-accent"
          >
            {editing ? 'Close' : 'Edit'}
          </button>
          <button type="button" onClick={remove} className="rounded-full border border-line px-4 py-2 text-sm text-loss">
            Delete
          </button>
        </div>
      </div>

      <div className="flex flex-wrap items-end gap-3 rounded-2xl border border-line bg-panel p-4 shadow-sm">
        <label className="flex flex-col gap-1 text-sm text-muted">
          Username
          <input className="rounded-lg border border-line bg-canvas px-3 py-2 text-ink" value={username} onChange={(event) => setUsername(event.target.value)} />
        </label>
        <label className="flex flex-col gap-1 text-sm text-muted">
          API password
          <input className="rounded-lg border border-line bg-canvas px-3 py-2 text-ink" type="password" value={password} onChange={(event) => setPassword(event.target.value)} />
        </label>
        <p className="pb-2 text-sm text-muted">Used only for save and delete.</p>
      </div>

      {actionError && <p className="text-sm text-loss">{actionError}</p>}

      {editing && (
        <TradeForm
          key={trade.id}
          trade={trade}
          submitting={submitting}
          onSubmit={save}
          onCancel={() => setEditing(false)}
        />
      )}

      <section className="grid gap-3 sm:grid-cols-2">
        {fields.map((field) => (
          <article key={field.label} className="rounded-2xl border border-line bg-panel p-4 shadow-sm">
            <p className="text-sm text-muted">{field.label}</p>
            <p className={`mt-1 text-lg font-medium ${field.color ?? ''}`}>{field.value}</p>
          </article>
        ))}
      </section>
    </div>
  )
}

export default TradeDetail
