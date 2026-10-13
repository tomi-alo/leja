export type Trade = {
  id: number
  symbol: string
  direction: 'BUY' | 'SELL'
  session: 'NY' | 'ASIA' | 'LONDON'
  positionSize: number
  contractSize: number
  entryPrice: number
  exitPrice: number
  stopLoss: number
  takeProfit: number
  pnl: number
  riskToRewardRatio: number | null
  reason: string | null
  outcome: 'WIN' | 'LOSS' | 'BREAK EVEN'
  executedAt: string
}

// The API validates these as strings, matching CreateTradeRequest in Java.
export type TradeWrite = {
  symbol: string
  direction: string
  session: string
  positionSize: string
  contractSize: string
  entryPrice: string
  exitPrice: string
  stopLoss: string
  takeProfit: string
  reason?: string
  executedAt?: string
}

export type Credentials = {
  username: string
  password: string
}
