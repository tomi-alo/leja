import { useState } from 'react'
import type { FormEvent } from 'react'
import type { Trade, TradeWrite } from './types'

const inputClass =
  'rounded-lg border border-line bg-canvas px-3 py-2 text-ink transition-colors duration-300 outline-none focus:border-accent'

type TradeFormProps = {
  trade: Trade | null
  submitting: boolean
  onSubmit: (draft: TradeWrite) => Promise<void>
  onCancel: () => void
}

function dateInputValue(date: Date) {
  const pad = (value: number) => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
}

function dateOnlyToInstant(value: string) {
  const [year, month, day] = value.split('-').map(Number)
  return new Date(year, month - 1, day, 12, 0, 0).toISOString()
}

function TradeForm({ trade, submitting, onSubmit, onCancel }: TradeFormProps) {
  const [symbol, setSymbol] = useState(trade?.symbol ?? '')
  const [direction, setDirection] = useState<string>(trade?.direction ?? 'BUY')
  const [session, setSession] = useState<string>(trade?.session ?? 'NY')
  const [positionSize, setPositionSize] = useState(trade ? String(trade.positionSize) : '')
  const [contractSize, setContractSize] = useState(trade ? String(trade.contractSize) : '')
  const [entryPrice, setEntryPrice] = useState(trade ? String(trade.entryPrice) : '')
  const [exitPrice, setExitPrice] = useState(trade ? String(trade.exitPrice) : '')
  const [stopLoss, setStopLoss] = useState(trade ? String(trade.stopLoss) : '')
  const [takeProfit, setTakeProfit] = useState(trade ? String(trade.takeProfit) : '')
  const [reason, setReason] = useState(trade?.reason ?? '')
  const [executedAt, setExecutedAt] = useState(trade ? dateInputValue(new Date(trade.executedAt)) : dateInputValue(new Date()))
  const [error, setError] = useState<string | null>(null)

  async function handleSubmit(event: FormEvent) {
    event.preventDefault()
    setError(null)

    const draft: TradeWrite = {
      symbol,
      direction,
      session,
      positionSize,
      contractSize,
      entryPrice,
      exitPrice,
      stopLoss,
      takeProfit,
      reason,
    }

    const today = dateInputValue(new Date())
    if (!executedAt || (!trade && executedAt === today)) {
      draft.executedAt = new Date().toISOString()
    } else if (trade && executedAt === dateInputValue(new Date(trade.executedAt))) {
      draft.executedAt = trade.executedAt
    } else {
      draft.executedAt = dateOnlyToInstant(executedAt)
    }

    try {
      await onSubmit(draft)
    } catch (caught) {
      setError(caught instanceof Error ? caught.message : 'Could not save trade')
    }
  }

  return (
    <form onSubmit={handleSubmit} className="animate-rise rounded-2xl border border-line bg-panel p-5 shadow-sm transition-colors duration-300">
      <h2 className="mb-4 text-lg font-medium">{trade ? 'Edit trade' : 'New trade'}</h2>
      <div className="grid gap-3 sm:grid-cols-2">
        <label className="flex flex-col gap-1 text-sm text-muted">
          Symbol
          <input className={inputClass} value={symbol} onChange={(event) => setSymbol(event.target.value)} required />
        </label>
        <label className="flex flex-col gap-1 text-sm text-muted">
          Direction
          <select className={inputClass} value={direction} onChange={(event) => setDirection(event.target.value)}>
            <option value="BUY">BUY</option>
            <option value="SELL">SELL</option>
          </select>
        </label>
        <label className="flex flex-col gap-1 text-sm text-muted">
          Session
          <select className={inputClass} value={session} onChange={(event) => setSession(event.target.value)}>
            <option value="NY">NY</option>
            <option value="ASIA">ASIA</option>
            <option value="LONDON">LONDON</option>
          </select>
        </label>
        <label className="flex flex-col gap-1 text-sm text-muted">
          Date
          <input className={inputClass} type="date" value={executedAt} onChange={(event) => setExecutedAt(event.target.value)} required />
          <span className="text-xs">Today saves the current time. Another day saves the date only.</span>
        </label>
        <label className="flex flex-col gap-1 text-sm text-muted">
          Position size
          <input className={inputClass} value={positionSize} onChange={(event) => setPositionSize(event.target.value)} required />
        </label>
        <label className="flex flex-col gap-1 text-sm text-muted">
          Contract size
          <input className={inputClass} value={contractSize} onChange={(event) => setContractSize(event.target.value)} required />
        </label>
        <label className="flex flex-col gap-1 text-sm text-muted">
          Entry
          <input className={inputClass} value={entryPrice} onChange={(event) => setEntryPrice(event.target.value)} required />
        </label>
        <label className="flex flex-col gap-1 text-sm text-muted">
          Exit
          <input className={inputClass} value={exitPrice} onChange={(event) => setExitPrice(event.target.value)} required />
        </label>
        <label className="flex flex-col gap-1 text-sm text-muted">
          Stop loss
          <input className={inputClass} value={stopLoss} onChange={(event) => setStopLoss(event.target.value)} required />
        </label>
        <label className="flex flex-col gap-1 text-sm text-muted">
          Take profit
          <input className={inputClass} value={takeProfit} onChange={(event) => setTakeProfit(event.target.value)} required />
        </label>
        <label className="flex flex-col gap-1 text-sm text-muted sm:col-span-2">
          Reason
          <input className={inputClass} value={reason} onChange={(event) => setReason(event.target.value)} maxLength={255} />
        </label>
      </div>
      <p className="mt-3 text-sm text-muted">
        BUY needs the stop below entry and the target above it. SELL is the reverse. The API checks this and calculates PnL.
      </p>
      {error && (
        <ul className="mt-3 text-sm text-loss">
          {error.split('\n').map((line) => (
            <li key={line}>{line}</li>
          ))}
        </ul>
      )}
      <div className="mt-4 flex gap-2">
        <button type="submit" disabled={submitting} className="rounded-full bg-accent px-4 py-2 text-sm font-medium text-on-accent transition-transform duration-300 hover:-translate-y-0.5 disabled:translate-y-0">
          {submitting ? 'Saving...' : 'Save'}
        </button>
        <button type="button" onClick={onCancel} className="rounded-full border border-line px-4 py-2 text-sm transition-colors duration-300 hover:border-accent">
          Cancel
        </button>
      </div>
    </form>
  )
}

export default TradeForm
