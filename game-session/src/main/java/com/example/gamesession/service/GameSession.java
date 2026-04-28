package com.example.gamesession.service;

import com.example.gamesession.model.EngineGameState;
import com.example.gamesession.model.MoveRecord;
import com.example.gamesession.model.SessionResponse;
import com.example.gamesession.model.SessionStatus;

import java.util.ArrayList;
import java.util.List;

final class GameSession {

    private final String sessionId;
    private final List<MoveRecord> moveHistory = new ArrayList<>();
    private SessionStatus status;
    private EngineGameState gameState;
    private String errorMessage;

    GameSession(String sessionId, EngineGameState gameState) {
        this.sessionId = sessionId;
        this.gameState = gameState;
        this.status = SessionStatus.CREATED;
    }

    String sessionId() {
        return sessionId;
    }

    SessionStatus status() {
        return status;
    }

    void setStatus(SessionStatus status) {
        this.status = status;
    }

    EngineGameState gameState() {
        return gameState;
    }

    void setGameState(EngineGameState gameState) {
        this.gameState = gameState;
    }

    int turnCount() {
        return moveHistory.size();
    }

    void addMove(MoveRecord moveRecord) {
        moveHistory.add(moveRecord);
    }

    void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    SessionResponse snapshot() {
        return new SessionResponse(
                sessionId,
                status,
                gameState,
                List.copyOf(moveHistory),
                errorMessage
        );
    }
}
