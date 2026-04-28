package com.example.gamesession.service;

import com.example.gamesession.client.EngineClient;
import com.example.gamesession.exception.SessionNotFoundException;
import com.example.gamesession.model.EngineGameState;
import com.example.gamesession.model.MoveRecord;
import com.example.gamesession.model.SessionEvent;
import com.example.gamesession.model.SessionResponse;
import com.example.gamesession.model.SessionStatus;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SessionService {

    private final EngineClient engineClient;
    private final MoveStrategy moveStrategy;
    private final SessionEventPublisher eventPublisher;
    private final long moveDelayMs;
    private final Map<String, GameSession> sessions = new ConcurrentHashMap<>();
    private final ExecutorService simulationExecutor = Executors.newCachedThreadPool(new SimulatorThreadFactory());

    public SessionService(
            EngineClient engineClient,
            MoveStrategy moveStrategy,
            SessionEventPublisher eventPublisher,
            @Value("${simulation.move-delay-ms}") long moveDelayMs
    ) {
        this.engineClient = engineClient;
        this.moveStrategy = moveStrategy;
        this.eventPublisher = eventPublisher;
        this.moveDelayMs = moveDelayMs;
    }

    public SessionResponse createSession() {
        String sessionId = UUID.randomUUID().toString();
        EngineGameState gameState = engineClient.getGame(sessionId);
        GameSession session = new GameSession(sessionId, gameState);
        sessions.put(sessionId, session);

        return startSession(session);
    }

    public SessionResponse getSession(String sessionId) {
        return snapshot(findSession(sessionId));
    }

    public SseEmitter subscribe(String sessionId) {
        return eventPublisher.subscribe(sessionId, getSession(sessionId));
    }

    @PreDestroy
    public void shutdown() {
        simulationExecutor.shutdownNow();
    }

    private SessionResponse startSession(GameSession session) {
        session.setStatus(SessionStatus.IN_PROGRESS);
        SessionResponse startedSession = session.snapshot();

        eventPublisher.publish(session.sessionId(), SessionEvent.from("STARTED", startedSession, null));
        simulationExecutor.execute(() -> runAutomatedGame(session));
        return startedSession;
    }

    private void runAutomatedGame(GameSession session) {
        try {
            while (true) {
                EngineGameState currentGameState = snapshot(session).gameState();
                if (isGameFinished(currentGameState)) {
                    completeSession(session);
                    return;
                }

                delayBetweenMoves();

                String player = currentGameState.nextPlayer();
                int position = moveStrategy.chooseMove(currentGameState, player);
                EngineGameState gameState = engineClient.submitMove(session.sessionId(), player, position);
                MoveRecord moveRecord;

                synchronized (session) {
                    moveRecord = new MoveRecord(
                            session.turnCount() + 1,
                            player,
                            position,
                            gameState,
                            Instant.now()
                    );
                    session.setGameState(gameState);
                    session.addMove(moveRecord);
                }

                publish(session, "MOVE", moveRecord);
            }
        } catch (RuntimeException exception) {
            failSession(session, exception);
        } finally {
            if (isTerminal(session)) {
                eventPublisher.complete(session.sessionId());
            }
        }
    }

    private void completeSession(GameSession session) {
        synchronized (session) {
            session.setStatus(SessionStatus.COMPLETED);
        }

        publish(session, "COMPLETED", null);
    }

    private void failSession(GameSession session, RuntimeException exception) {
        synchronized (session) {
            session.setStatus(SessionStatus.FAILED);
            session.setErrorMessage(exception.getMessage());
        }

        publish(session, "FAILED", null);
    }

    private GameSession findSession(String sessionId) {
        GameSession session = sessions.get(sessionId);
        if (session == null) {
            throw new SessionNotFoundException("Session " + sessionId + " was not found.");
        }
        return session;
    }

    private void publish(GameSession session, String type, MoveRecord moveRecord) {
        eventPublisher.publish(session.sessionId(), SessionEvent.from(type, snapshot(session), moveRecord));
    }

    private boolean isGameFinished(EngineGameState gameState) {
        return !"IN_PROGRESS".equals(gameState.status());
    }

    private boolean isTerminal(GameSession session) {
        synchronized (session) {
            return session.status() == SessionStatus.COMPLETED || session.status() == SessionStatus.FAILED;
        }
    }

    private SessionResponse snapshot(GameSession session) {
        synchronized (session) {
            return session.snapshot();
        }
    }

    private void delayBetweenMoves() {
        if (moveDelayMs <= 0) {
            return;
        }

        try {
            Thread.sleep(moveDelayMs);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Simulation was interrupted.", exception);
        }
    }

    private static final class SimulatorThreadFactory implements java.util.concurrent.ThreadFactory {

        private final AtomicInteger sequence = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable task) {
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.setName("game-session-simulator-" + sequence.getAndIncrement());
            return thread;
        }
    }
}
