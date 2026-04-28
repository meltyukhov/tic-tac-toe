package com.example.gamesession.model;

import java.time.Instant;

public record MoveRecord(
        int turn,
        String player,
        int position,
        EngineGameState gameState,
        Instant playedAt
) {
}
