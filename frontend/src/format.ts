import type { Trade } from './types'

export function money(value: number) {
  const amount = Math.abs(value).toFixed(2)
  if (value > 0) return `+$${amount}`
  if (value < 0) return `-$${amount}`
  return `$${amount}`
}

export function price(value: number) {
  return Number(value).toFixed(4)
}

export function ratio(value: number | null | undefined) {
  if (value == null || Number.isNaN(value)) return '—'
  return value.toFixed(2)
}

export function pnlColor(value: number) {
  if (value > 0) return 'text-win'
  if (value < 0) return 'text-loss'
  return 'text-muted'
}

export function when(iso: string) {
  return new Date(iso).toLocaleString(undefined, {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })
}

export function dayLabel(key: string) {
  const [year, month, day] = key.split('-').map(Number)
  return new Date(year, month - 1, day).toLocaleDateString(undefined, {
    month: 'long',
    day: 'numeric',
    year: 'numeric',
  })
}

export function dayKey(iso: string) {
  return dateKey(new Date(iso))
}

export function dateKey(date: Date) {
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${date.getFullYear()}-${month}-${day}`
}

export function todayKey() {
  return dateKey(new Date())
}

export function totalPnl(trades: Trade[]) {
  return trades.reduce((sum, trade) => sum + trade.pnl, 0)
}

export function averagePnl(trades: Trade[]) {
  if (trades.length === 0) return 0
  return totalPnl(trades) / trades.length
}

export function winRate(trades: Trade[]) {
  if (trades.length === 0) return 0
  const wins = trades.filter((trade) => trade.outcome === 'WIN').length
  return (wins / trades.length) * 100
}

export function averageRiskReward(trades: Trade[]) {
  const values = trades
    .map((trade) => trade.riskToRewardRatio)
    .filter((value): value is number => value != null && !Number.isNaN(value))
  if (values.length === 0) return 0
  return values.reduce((sum, value) => sum + value, 0) / values.length
}

export function buySellCount(trades: Trade[]) {
  return {
    buys: trades.filter((trade) => trade.direction === 'BUY').length,
    sells: trades.filter((trade) => trade.direction === 'SELL').length,
  }
}

export function statsBySession(trades: Trade[]) {
  const order = ['NY', 'ASIA', 'LONDON'] as const
  return order.map((session) => {
    const rows = trades.filter((trade) => trade.session === session)
    return {
      session,
      trades: rows.length,
      winRate: winRate(rows),
      pnl: totalPnl(rows),
      averagePnl: averagePnl(rows),
    }
  })
}

export function statsBySymbol(trades: Trade[]) {
  const groups = new Map<string, Trade[]>()
  for (const trade of trades) {
    const rows = groups.get(trade.symbol) ?? []
    rows.push(trade)
    groups.set(trade.symbol, rows)
  }

  return [...groups.entries()]
    .map(([symbol, rows]) => ({
      symbol,
      trades: rows.length,
      winRate: winRate(rows),
      pnl: totalPnl(rows),
      averagePnl: averagePnl(rows),
    }))
    .sort((a, b) => b.trades - a.trades)
}

export function pnlByDay(trades: Trade[]) {
  const totals = new Map<string, { pnl: number; count: number }>()
  for (const trade of trades) {
    const key = dayKey(trade.executedAt)
    const current = totals.get(key) ?? { pnl: 0, count: 0 }
    current.pnl += trade.pnl
    current.count += 1
    totals.set(key, current)
  }
  return totals
}
