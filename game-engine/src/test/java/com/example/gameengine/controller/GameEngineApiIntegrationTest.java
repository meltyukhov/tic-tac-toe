package com.example.gameengine.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GameEngineApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("creates an empty in-progress game when first read")
    void firstReadCreatesGame() throws Exception {
        mockMvc.perform(get("/games/new-game"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value("new-game"))
                .andExpect(jsonPath("$.board.length()").value(9))
                .andExpect(jsonPath("$.board[0]").value(""))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.winner").doesNotExist())
                .andExpect(jsonPath("$.nextPlayer").value("X"))
                .andExpect(jsonPath("$.movesPlayed").value(0));
    }

    @Test
    @DisplayName("accepts legal moves and reports a win")
    void winningGame() throws Exception {
        String gameId = "x-wins";

        play(gameId, "X", 0)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nextPlayer").value("O"))
                .andExpect(jsonPath("$.movesPlayed").value(1));
        play(gameId, "O", 3).andExpect(status().isOk());
        play(gameId, "X", 1).andExpect(status().isOk());
        play(gameId, "O", 4).andExpect(status().isOk());

        play(gameId, "X", 2)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.board[0]").value("X"))
                .andExpect(jsonPath("$.board[1]").value("X"))
                .andExpect(jsonPath("$.board[2]").value("X"))
                .andExpect(jsonPath("$.status").value("WIN"))
                .andExpect(jsonPath("$.winner").value("X"))
                .andExpect(jsonPath("$.nextPlayer").doesNotExist())
                .andExpect(jsonPath("$.movesPlayed").value(5));
    }

    @Test
    @DisplayName("rejects moves with an invalid player")
    void invalidPlayer() throws Exception {
        play("invalid-player", "Z", 0)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Player must be X or O."));
    }

    @Test
    @DisplayName("rejects moves outside the board")
    void invalidPosition() throws Exception {
        play("invalid-position", "X", 9)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Position must be between 0 and 8."));
    }

    @Test
    @DisplayName("rejects moves played out of turn")
    void wrongTurn() throws Exception {
        play("wrong-turn", "O", 0)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("It is X's turn."));
    }

    @Test
    @DisplayName("rejects moves into occupied cells")
    void occupiedCell() throws Exception {
        String gameId = "occupied-cell";

        play(gameId, "X", 0).andExpect(status().isOk());
        play(gameId, "O", 0)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Position 0 is already occupied."));
    }

    @Test
    @DisplayName("rejects moves after the game has finished")
    void finishedGame() throws Exception {
        String gameId = "finished-game";

        play(gameId, "X", 0).andExpect(status().isOk());
        play(gameId, "O", 3).andExpect(status().isOk());
        play(gameId, "X", 1).andExpect(status().isOk());
        play(gameId, "O", 4).andExpect(status().isOk());
        play(gameId, "X", 2).andExpect(status().isOk());

        play(gameId, "O", 5)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Game finished-game has already finished."));
    }

    private ResultActions play(String gameId, String player, int position) throws Exception {
        return mockMvc.perform(post("/games/{gameId}/move", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"player":"%s","position":%d}
                        """.formatted(player, position)));
    }
}
