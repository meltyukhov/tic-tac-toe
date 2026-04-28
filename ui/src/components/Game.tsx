import ErrorBanner from "./ErrorBanner.tsx";
import GameBoardPanel from "./board/GameBoardPanel.tsx";
import GameHeader from "./GameHeader.tsx";
import GameLayout from "./GameLayout.tsx";
import api, {type MoveRecord, type SessionEvent, type SessionResponse} from "../api";
import {useEffect, useMemo, useRef, useState} from "react";
import GameSidebar from "./GameSidebar.tsx";

const WINNING_LINES = [
  [0, 1, 2],
  [3, 4, 5],
  [6, 7, 8],
  [0, 3, 6],
  [1, 4, 7],
  [2, 5, 8],
  [0, 4, 8],
  [2, 4, 6],
];

const EMPTY_BOARD = ["", "", "", "", "", "", "", "", ""];

const getWinningCells = (board: string[]) => {
  for (const line of WINNING_LINES) {
    const [a, b, c] = line;
    if (board[a] && board[a] === board[b] && board[a] === board[c]) {
      return line;
    }
  }

  return [];
};

const getErrorMessage = (error: unknown) => {
  return error instanceof Error ? error.message : "Something went wrong.";
};

const applySessionEvent = (currentSession: SessionResponse | null, event: SessionEvent) => {
  if (!currentSession) {
    return currentSession;
  }

  const nextMoveHistory = event.move
    ? appendMove(currentSession.moveHistory, {
      ...event.move,
      gameState: event.gameState,
    })
    : currentSession.moveHistory;

  return {
    ...currentSession,
    errorMessage: event.errorMessage,
    gameState: event.gameState,
    moveHistory: nextMoveHistory,
    status: event.status,
  };
};

const appendMove = (moveHistory: MoveRecord[], move: MoveRecord) => {
  if (moveHistory.some((existingMove) => existingMove.turn === move.turn)) {
    return moveHistory;
  }

  return [...moveHistory, move];
};

const Game = () => {
  const [session, setSession] = useState<SessionResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isRunning, setIsRunning] = useState(false);
  const eventSourceRef = useRef<EventSource | null>(null);

  const board = session?.gameState.board ?? EMPTY_BOARD;
  const winningCells = useMemo(() => getWinningCells(board), [board]);

  useEffect(() => {
    return () => {
      eventSourceRef.current?.close();
    };
  }, []);

  const handleSessionEvent = (event: MessageEvent<string>) => {
    const sessionEvent = JSON.parse(event.data) as SessionEvent;
    setSession((currentSession) => applySessionEvent(currentSession, sessionEvent));

    if (sessionEvent.type === "COMPLETED" || sessionEvent.type === "FAILED") {
      setIsRunning(false);
      eventSourceRef.current?.close();
    }
    if (sessionEvent.type === "FAILED") {
      setError(sessionEvent.errorMessage ?? "Simulation failed.");
    }
  };

  const startSimulation = async () => {
    setError(null);
    setIsRunning(true);
    eventSourceRef.current?.close();

    try {
      const createdSession = await api.createSession();
      setSession(createdSession);

      const source = api.subscribeToSession(createdSession.sessionId);
      eventSourceRef.current = source;
      source.onmessage = handleSessionEvent;
      source.onerror = () => {
        if (source.readyState !== EventSource.CLOSED) {
          setError("Live updates disconnected.");
        }
      };
    } catch (requestError) {
      eventSourceRef.current?.close();
      setError(getErrorMessage(requestError));
      setIsRunning(false);
    }
  };

  return (
    <GameLayout
      board={<GameBoardPanel board={board} winningCells={winningCells}/>}
      feedback={error ? <ErrorBanner message={error}/> : null}
      header={<GameHeader isRunning={isRunning} onStartSimulation={startSimulation}/>}
      sidebar={<GameSidebar session={session}/>}
    />
  );
};

export default Game;
