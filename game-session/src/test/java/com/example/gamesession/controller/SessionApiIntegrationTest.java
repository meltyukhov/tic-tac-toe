package com.example.gamesession.controller;

import com.example.gamesession.client.EngineClient;
import com.example.gamesession.exception.EngineCommunicationException;
import com.example.gamesession.model.EngineGameState;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "simulation.move-delay-ms=0")
@AutoConfigureMockMvc
class SessionApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FakeEngineClient engineClient;

    @BeforeEach
    void resetEngine() {
        engineClient.reset();
    }

    @Test
    @DisplayName("creates a session, automatically plays the game, and stores move history")
    void automatedSessionFlow() throws Exception {
        String createdSession = createSession()
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.gameState.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.moveHistory.length()").value(0))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String sessionId = JsonPath.read(createdSession, "$.sessionId");
        String completedSession = waitForTerminalSession(sessionId);
        String gameStatus = JsonPath.read(completedSession, "$.gameState.status");
        int movesPlayed = JsonPath.read(completedSession, "$.gameState.movesPlayed");
        List<Map<String, Object>> moveHistory = JsonPath.read(completedSession, "$.moveHistory");

        assertEquals("COMPLETED", JsonPath.read(completedSession, "$.status"));
        assertTrue(Set.of("WIN", "DRAW").contains(gameStatus));
        assertTrue(movesPlayed >= 5 && movesPlayed <= 9);
        assertEquals(movesPlayed, moveHistory.size());
        assertEquals(1, engineClient.lookupCount(sessionId));
        assertEquals(movesPlayed, engineClient.moveCount(sessionId));

        for (int index = 0; index < moveHistory.size(); index++) {
            Map<String, Object> move = moveHistory.get(index);
            Map<String, Object> moveGameState = castMap(move.get("gameState"));

            assertEquals(index + 1, move.get("turn"));
            assertTrue(Set.of("X", "O").contains(move.get("player")));
            assertTrue((Integer) move.get("position") >= 0 && (Integer) move.get("position") <= 8);
            assertEquals(index + 1, moveGameState.get("movesPlayed"));
            assertFalse(((String) move.get("playedAt")).isBlank());
        }
    }

    @Test
    @DisplayName("returns session not found errors through the API")
    void missingSession() throws Exception {
        mockMvc.perform(get("/sessions/missing-session"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Session missing-session was not found."));
    }

    @Test
    @DisplayName("does not expose a separate simulation trigger endpoint")
    void noSimulationEndpoint() throws Exception {
        mockMvc.perform(post("/sessions/any-session/simulate"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("returns bad gateway when the engine rejects session initialization")
    void engineLookupFailure() throws Exception {
        engineClient.failLookups();

        createSession()
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.message", startsWith("Unable to initialize engine game.")));
    }

    @Test
    @DisplayName("marks a session as failed when the engine rejects an automated move")
    void engineMoveFailure() throws Exception {
        engineClient.failMoves();

        String createdSession = createSession()
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String failedSession = waitForTerminalSession(JsonPath.read(createdSession, "$.sessionId"));

        assertEquals("FAILED", JsonPath.read(failedSession, "$.status"));
        assertEquals("Unable to submit engine move.", JsonPath.read(failedSession, "$.errorMessage"));
    }

    private ResultActions createSession() throws Exception {
        return mockMvc.perform(post("/sessions"));
    }

    private String getSession(String sessionId) throws Exception {
        return mockMvc.perform(get("/sessions/{sessionId}", sessionId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    private String waitForTerminalSession(String sessionId) throws Exception {
        for (int attempt = 0; attempt < 50; attempt++) {
            String session = getSession(sessionId);
            String status = JsonPath.read(session, "$.status");
            if ("COMPLETED".equals(status) || "FAILED".equals(status)) {
                return session;
            }

            Thread.sleep(50);
        }

        fail("Session " + sessionId + " did not reach a terminal state.");
        return "";
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Object value) {
        return (Map<String, Object>) value;
    }

    @TestConfiguration
    static class FakeEngineConfiguration {

        @Bean
        @Primary
        FakeEngineClient fakeEngineClient() {
            return new FakeEngineClient();
        }
    }

    static final class FakeEngineClient implements EngineClient {

        private static final int BOARD_SIZE = 9;

        private final Map<String, List<String>> games = new ConcurrentHashMap<>();
        private final Map<String, Integer> lookupCounts = new ConcurrentHashMap<>();
        private final Map<String, Integer> moveCounts = new ConcurrentHashMap<>();
        private boolean failLookups;
        private boolean failMoves;

        @Override
        public EngineGameState getGame(String gameId) {
            if (failLookups) {
                throw new EngineCommunicationException("Unable to initialize engine game.");
            }

            lookupCounts.merge(gameId, 1, Integer::sum);
            return snapshot(gameId, game(gameId));
        }

        @Override
        public EngineGameState submitMove(String gameId, String player, int position) {
            if (failMoves) {
                throw new EngineCommunicationException("Unable to submit engine move.");
            }

            List<String> board = game(gameId);
            board.set(position, player);
            moveCounts.merge(gameId, 1, Integer::sum);
            return snapshot(gameId, board);
        }

        void reset() {
            games.clear();
            lookupCounts.clear();
            moveCounts.clear();
            failLookups = false;
            failMoves = false;
        }

        void failLookups() {
            failLookups = true;
        }

        void failMoves() {
            failMoves = true;
        }

        int lookupCount(String gameId) {
            return lookupCounts.getOrDefault(gameId, 0);
        }

        int moveCount(String gameId) {
            return moveCounts.getOrDefault(gameId, 0);
        }

        private List<String> game(String gameId) {
            return games.computeIfAbsent(gameId, ignored -> new ArrayList<>(Collections.nCopies(BOARD_SIZE, "")));
        }

        private EngineGameState snapshot(String gameId, List<String> board) {
            int movesPlayed = (int) board.stream().filter(value -> !value.isBlank()).count();
            String status = movesPlayed == BOARD_SIZE ? "DRAW" : "IN_PROGRESS";
            String winner = null;
            String nextPlayer = "IN_PROGRESS".equals(status)
                    ? (movesPlayed % 2 == 0 ? "X" : "O")
                    : null;
            return new EngineGameState(gameId, List.copyOf(board), status, winner, nextPlayer, movesPlayed);
        }
    }
}
