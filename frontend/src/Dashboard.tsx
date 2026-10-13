import { Link } from 'react-router-dom'
import { averagePnl, averageRiskReward, money, pnlColor, statsBySession, statsBySymbol, totalPnl, winRate } from './format'
import { useTrades } from './useTrades'

function Dashboard() {
  const { trades, loading, error } = useTrades()

  if (loading) return <p className="text-muted">Loading trades...</p>
  if (error) return <p className="text-loss">{error}</p>

  const sessions = statsBySession(trades)
  const symbols = statsBySymbol(trades)
  const sessionPeak = Math.max(...sessions.map((row) => Math.abs(row.pnl)), 0)
  const stats = [
    { label: 'Total PnL (USD)', value: money(totalPnl(trades)), color: pnlColor(totalPnl(trades)) },
    { label: 'Avg PnL (USD)', value: money(averagePnl(trades)), color: pnlColor(averagePnl(trades)) },
    { label: 'Win rate', value: `${winRate(trades).toFixed(0)}%`, color: 'text-ink' },
    { label: 'Trades', value: String(trades.length), color: 'text-ink' },
    { label: 'Avg R:R', value: averageRiskReward(trades).toFixed(2), color: 'text-ink' },
  ]

  return (
    <div className="flex flex-col gap-6">
      <h1 className="text-2xl font-medium">Dashboard</h1>
      <section className="grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
        {stats.map((stat, index) => (
          <article
            key={stat.label}
            style={{ animationDelay: `${index * 70}ms` }}
            className="animate-rise rounded-2xl border border-line bg-panel p-4 shadow-sm transition-all duration-300 hover:-translate-y-0.5 hover:border-accent/40"
          >
            <p className="text-sm text-muted">{stat.label}</p>
            <p className={`mt-2 text-2xl font-medium ${stat.color}`}>{stat.value}</p>
          </article>
        ))}
      </section>
      <section className="grid gap-4 lg:grid-cols-2">
        <article className="overflow-hidden rounded-2xl border border-line bg-panel shadow-sm transition-colors duration-300">
          <h2 className="border-b border-line px-4 py-3 text-sm font-medium text-muted">Latest trades</h2>
          <ul>
            {trades.slice(0, 5).map((trade) => (
              <li key={trade.id} className="border-b border-line last:border-b-0">
                <Link to={`/journal/${trade.id}`} className="grid grid-cols-[1fr_auto] items-center gap-4 px-4 py-3 transition-colors duration-300 hover:bg-accent/5">
                  <span>
                    {trade.symbol} <span className="text-muted">{trade.direction}</span>
                  </span>
                  <span className={`text-right tabular-nums ${pnlColor(trade.pnl)}`}>{money(trade.pnl)}</span>
                </Link>
              </li>
            ))}
            {trades.length === 0 && <li className="px-4 py-3 text-muted">No trades yet.</li>}
          </ul>
        </article>
        <article className="flex flex-col overflow-hidden rounded-2xl border border-line bg-panel shadow-sm transition-colors duration-300">
          <table className="w-full text-left text-sm">
            <thead className="text-muted">
              <tr>
                <th className="px-4 py-3 font-medium">Session</th>
                <th className="px-4 py-3 text-right font-medium">Trades</th>
                <th className="px-4 py-3 text-right font-medium">Win rate</th>
                <th className="px-4 py-3 text-right font-medium">PnL</th>
              </tr>
            </thead>
            <tbody>
              {sessions.map((row) => (
                <tr key={row.session} className="border-t border-line">
                  <td className="px-4 py-3">{row.session}</td>
                  <td className="px-4 py-3 text-right tabular-nums">{row.trades}</td>
                  <td className="px-4 py-3 text-right tabular-nums">{row.winRate.toFixed(0)}%</td>
                  <td className={`px-4 py-3 text-right tabular-nums ${pnlColor(row.pnl)}`}>{money(row.pnl)}</td>
                </tr>
              ))}
            </tbody>
          </table>
          <div className="flex flex-1 flex-col justify-evenly gap-3 border-t border-line px-4 py-4">
            {sessions.map((row) => (
              <div
                key={`${row.session}-bar`}
                aria-label={`${row.session} PnL ${money(row.pnl)}`}
                className="grid grid-cols-[4.25rem_1fr] items-center gap-3"
              >
                <span className="text-xs text-muted">{row.session}</span>
                <div className="h-2.5 overflow-hidden rounded-full bg-canvas">
                  <div
                    className={`h-full rounded-full ${row.pnl > 0 ? 'bg-win' : row.pnl < 0 ? 'bg-loss' : 'bg-line'}`}
                    style={{ width: sessionPeak === 0 ? '0%' : `${Math.max((Math.abs(row.pnl) / sessionPeak) * 100, row.pnl === 0 ? 0 : 8)}%` }}
                  />
                </div>
              </div>
            ))}
          </div>
        </article>
      </section>
      <section className="overflow-hidden rounded-2xl border border-line bg-panel shadow-sm transition-colors duration-300">
        {symbols.length === 0 ? (
          <p className="px-4 py-3 text-sm text-muted">No trades yet.</p>
        ) : (
          <table className="w-full text-left text-sm">
            <thead className="text-muted">
              <tr>
                <th className="px-4 py-3 font-medium">Symbol</th>
                <th className="px-4 py-3 text-right font-medium">Trades</th>
                <th className="px-4 py-3 text-right font-medium">Win rate</th>
                <th className="px-4 py-3 text-right font-medium">Avg PnL</th>
                <th className="px-4 py-3 text-right font-medium">Total PnL</th>
              </tr>
            </thead>
            <tbody>
              {symbols.map((row) => (
                <tr key={row.symbol} className="border-t border-line">
                  <td className="px-4 py-3">{row.symbol}</td>
                  <td className="px-4 py-3 text-right tabular-nums">{row.trades}</td>
                  <td className="px-4 py-3 text-right tabular-nums">{row.winRate.toFixed(0)}%</td>
                  <td className={`px-4 py-3 text-right tabular-nums ${pnlColor(row.averagePnl)}`}>{money(row.averagePnl)}</td>
                  <td className={`px-4 py-3 text-right tabular-nums ${pnlColor(row.pnl)}`}>{money(row.pnl)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </div>
  )
}

export default Dashboard
