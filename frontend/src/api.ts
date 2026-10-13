import type { Credentials, Trade, TradeWrite } from './types'

// Dev calls stay on this Vite server so the proxy can forward them.
// A production build calls the Render URL directly.
export function apiUrl(path: string) {
  const base = import.meta.env.DEV ? '' : import.meta.env.VITE_API_URL
  return `${base}${path}`
}

export async function getTrades(): Promise<Trade[]> {
  const response = await fetch(apiUrl('/api/v1/trades'))
  if (!response.ok) {
    throw new Error('Could not load trades')
  }
  return response.json()
}

export async function getTrade(id: number): Promise<Trade> {
  const response = await fetch(apiUrl(`/api/v1/trades/${id}`))
  if (response.status === 404) {
    throw new Error('Trade not found')
  }
  if (!response.ok) {
    throw new Error('Could not load trade')
  }
  return response.json()
}

export async function createTrade(trade: TradeWrite, credentials: Credentials) {
  await write('/api/v1/trades', 'POST', trade, credentials)
}

export async function updateTrade(id: number, trade: TradeWrite, credentials: Credentials) {
  await write(`/api/v1/trades/${id}`, 'PATCH', trade, credentials)
}

export async function deleteTrade(id: number, credentials: Credentials) {
  await write(`/api/v1/trades/${id}`, 'DELETE', undefined, credentials)
}

async function write(path: string, method: string, body: TradeWrite | undefined, credentials: Credentials) {
  const response = await fetch(apiUrl(path), {
    method,
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Basic ${btoa(`${credentials.username}:${credentials.password}`)}`,
    },
    body: body ? JSON.stringify(body) : undefined,
  })

  if (!response.ok) {
    throw new Error(await errorMessage(response))
  }
}

async function errorMessage(response: Response) {
  if (response.status === 401) {
    return 'Wrong username or password'
  }

  try {
    const body = (await response.json()) as { errors?: string[] }
    if (body.errors && body.errors.length > 0) {
      return body.errors.join('\n')
    }
  } catch {
    // Some failures have no JSON body.
  }

  return `Request failed (${response.status})`
}
