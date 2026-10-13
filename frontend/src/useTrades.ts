import { useCallback, useEffect, useState } from 'react'
import { getTrades } from './api'
import type { Trade } from './types'

function newestFirst(data: Trade[]) {
  return [...data].sort((a, b) => b.executedAt.localeCompare(a.executedAt))
}

export function useTrades() {
  const [trades, setTrades] = useState<Trade[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false

    getTrades()
      .then((data) => {
        if (!cancelled) setTrades(newestFirst(data))
      })
      .catch(() => {
        if (!cancelled) setError('Could not load trades. Is the API running?')
      })
      .finally(() => {
        if (!cancelled) setLoading(false)
      })

    return () => {
      cancelled = true
    }
  }, [])

  const reload = useCallback(() => {
    setLoading(true)
    setError(null)
    getTrades()
      .then((data) => setTrades(newestFirst(data)))
      .catch(() => setError('Could not load trades. Is the API running?'))
      .finally(() => setLoading(false))
  }, [])

  return { trades, loading, error, reload }
}
