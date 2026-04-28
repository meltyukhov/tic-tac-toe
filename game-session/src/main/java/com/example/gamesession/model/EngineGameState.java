package com.example.gamesession.model;

import java.util.List;

public record EngineGameState(
        String gameId,
        List<String> board,
        String status,
        String winner,
        String nextPlayer,
        int movesPlayed
) {
}
