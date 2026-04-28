package com.example.gamesession.service;

import com.example.gamesession.model.EngineGameState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MoveStrategyTest {

    private final MoveStrategy moveStrategy = new MoveStrategy();

    @Test
    @DisplayName("chooses a winning move before fallback positions")
    void winningMove() {
        int move = moveStrategy.chooseMove(state(
                "X", "X", "",
                "", "", "",
                "", "", ""
        ), "X");

        assertEquals(2, move);
    }

    @Test
    @DisplayName("chooses a blocking move before fallback positions")
    void blockingMove() {
        int move = moveStrategy.chooseMove(state(
                "O", "O", "",
                "", "X", "",
                "", "", ""
        ), "X");

        assertEquals(2, move);
    }

    @Test
    @DisplayName("chooses an available corner when the center is taken")
    void availableCorner() {
        int move = moveStrategy.chooseMove(state(
                "", "", "",
                "", "X", "",
                "", "", ""
        ), "O");

        assertTrue(Set.of(0, 2, 6, 8).contains(move));
    }

    @Test
    @DisplayName("chooses an available edge when the center and corners are taken")
    void availableEdge() {
        int move = moveStrategy.chooseMove(state(
                "X", "", "O",
                "", "X", "",
                "O", "", "X"
        ), "O");

        assertTrue(Set.of(1, 3, 5, 7).contains(move));
    }

    private EngineGameState state(String... board) {
        return new EngineGameState("game-id", List.of(board), "IN_PROGRESS", null, "X", 0);
    }
}
