import { useCallback, useEffect, useState } from 'react'
import { getTrades } from './api'
import type { Trade } from './types'

export function useTrades() {
  const [trades, setTrades] = useState<Trade[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const reload = useCallback(() => {
    setLoading(true)
    setError(null)
    getTrades()
      .then((data) => {
        const newestFirst = [...data].sort((a, b) => b.executedAt.localeCompare(a.executedAt))
        setTrades(newestFirst)
      })
      .catch(() => setError('Could not load trades. Is the API running?'))
      .finally(() => setLoading(false))
  }, [])

  useEffect(() => {
    reload()
  }, [reload])

  return { trades, loading, error, reload }
}
