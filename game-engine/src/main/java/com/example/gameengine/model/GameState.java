package com.example.gameengine.model;

import java.util.List;

public record GameState(
        String gameId,
        List<String> board,
        GameStatus status,
        String winner,
        String nextPlayer,
        int movesPlayed
) {
}
