package com.example.gameengine.service;

import com.example.gameengine.exception.GameAlreadyFinishedException;
import com.example.gameengine.exception.InvalidMoveException;
import com.example.gameengine.model.GameState;
import com.example.gameengine.model.GameStatus;
import com.example.gameengine.model.MoveRequest;
import com.example.gameengine.model.PlayerSymbol;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameEngineService {

    private static final int BOARD_SIZE = 9;
    private static final int[][] WINNING_LINES = {
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8},
            {0, 3, 6},
            {1, 4, 7},
            {2, 5, 8},
            {0, 4, 8},
            {2, 4, 6}
    };

    private final Map<String, MutableGame> games = new ConcurrentHashMap<>();

    public GameState getGame(String gameId) {
        return games.computeIfAbsent(gameId, MutableGame::new).snapshot();
    }

    public GameState makeMove(String gameId, MoveRequest request) {
        MutableGame game = games.computeIfAbsent(gameId, MutableGame::new);

        synchronized (game) {
            PlayerSymbol player = parsePlayer(request.player());
            validateMove(game, player, request.position());

            game.board.set(request.position(), player.name());
            game.movesPlayed++;

            if (hasWon(game.board, player)) {
                game.status = GameStatus.WIN;
                game.winner = player.name();
                game.nextPlayer = null;
            } else if (game.movesPlayed == BOARD_SIZE) {
                game.status = GameStatus.DRAW;
                game.nextPlayer = null;
            } else {
                game.nextPlayer = opponent(player).name();
            }

            return game.snapshot();
        }
    }

    private PlayerSymbol parsePlayer(String value) {
        try {
            return PlayerSymbol.from(value);
        } catch (IllegalArgumentException exception) {
            throw new InvalidMoveException("Player must be X or O.");
        }
    }

    private void validateMove(MutableGame game, PlayerSymbol player, int position) {
        if (game.status != GameStatus.IN_PROGRESS) {
            throw new GameAlreadyFinishedException("Game " + game.gameId + " has already finished.");
        }
        if (position < 0 || position >= BOARD_SIZE) {
            throw new InvalidMoveException("Position must be between 0 and 8.");
        }
        if (!game.board.get(position).isBlank()) {
            throw new InvalidMoveException("Position " + position + " is already occupied.");
        }
        if (!player.name().equals(game.nextPlayer)) {
            throw new InvalidMoveException("It is " + game.nextPlayer + "'s turn.");
        }
    }

    private boolean hasWon(List<String> board, PlayerSymbol player) {
        String symbol = player.name();

        for (int[] line : WINNING_LINES) {
            if (symbol.equals(board.get(line[0]))
                    && symbol.equals(board.get(line[1]))
                    && symbol.equals(board.get(line[2]))) {
                return true;
            }
        }

        return false;
    }

    private PlayerSymbol opponent(PlayerSymbol player) {
        return player == PlayerSymbol.X ? PlayerSymbol.O : PlayerSymbol.X;
    }

    private static final class MutableGame {

        private final String gameId;
        private final List<String> board = new ArrayList<>(Collections.nCopies(BOARD_SIZE, ""));
        private GameStatus status = GameStatus.IN_PROGRESS;
        private String winner;
        private String nextPlayer = PlayerSymbol.X.name();
        private int movesPlayed;

        private MutableGame(String gameId) {
            this.gameId = gameId;
        }

        private GameState snapshot() {
            return new GameState(
                    gameId,
                    List.copyOf(board),
                    status,
                    winner,
                    nextPlayer,
                    movesPlayed
            );
        }
    }
}
