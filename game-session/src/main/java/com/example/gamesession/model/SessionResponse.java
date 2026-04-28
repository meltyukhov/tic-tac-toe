package com.example.gamesession.model;

import java.util.List;

public record SessionResponse(
        String sessionId,
        SessionStatus status,
        EngineGameState gameState,
        List<MoveRecord> moveHistory,
        String errorMessage
) {
}
