export type GameStatus = "IN_PROGRESS" | "WIN" | "DRAW";
export type SessionStatus = "CREATED" | "IN_PROGRESS" | "COMPLETED" | "FAILED";

export type EngineGameState = {
  gameId: string;
  board: string[];
  status: GameStatus;
  winner: string | null;
  nextPlayer: string | null;
  movesPlayed: number;
};

export type MoveEvent = {
  turn: number;
  player: string;
  position: number;
  playedAt: string;
};

export type MoveRecord = MoveEvent & {
  gameState: EngineGameState;
};

export type SessionResponse = {
  sessionId: string;
  status: SessionStatus;
  gameState: EngineGameState;
  moveHistory: MoveRecord[];
  errorMessage: string | null;
};

export type SessionEvent = {
  type: "SNAPSHOT" | "STARTED" | "MOVE" | "COMPLETED" | "FAILED";
  status: SessionStatus;
  gameState: EngineGameState;
  move: MoveEvent | null;
  errorMessage: string | null;
};

const HOSTNAME = import.meta.env.VITE_SESSION_API_URL ?? "http://localhost:8080";

const request = async <T>(path: string, options?: RequestInit): Promise<T> => {
  const response = await fetch(`${HOSTNAME}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...options?.headers,
    },
    ...options,
  });

  if (!response.ok) {
    const errorBody = await response.json().catch(() => null) as { message?: string } | null;
    throw new Error(errorBody?.message ?? `Request failed with status ${response.status}`);
  }

  return response.json() as Promise<T>;
};

const createSession = () => {
  return request<SessionResponse>("/sessions", {method: "POST"});
};

const subscribeToSession = (sessionId: string) => {
  return new EventSource(`${HOSTNAME}/sessions/${sessionId}/events`);
};

const api = {
  createSession,
  subscribeToSession,
};

export default api;
