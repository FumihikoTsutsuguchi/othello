const BASE = import.meta.env.VITE_API_BASE || 'http://localhost:8080/api'

export async function createGame() {
  const res = await fetch(`${BASE}/games`, { method: 'POST' })
  return await res.json()
}

export async function getGame(id) {
  const res = await fetch(`${BASE}/games/${id}`)
  return await res.json()
}

export async function legalMoves(id, player) {
  const q = player ? `?player=${player}` : ''
  const res = await fetch(`${BASE}/games/${id}/legal-moves${q}`)
  return await res.json()
}

export async function play(id, x, y) {
  const res = await fetch(`${BASE}/games/${id}/moves`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ x, y })
  })
  if (!res.ok) throw new Error(await res.text())
  return await res.json()
}