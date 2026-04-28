package com.example.gamesession.service;

import com.example.gamesession.model.EngineGameState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MoveStrategy {

    private static final int[] CENTER = {4};
    private static final int[] CORNERS = {0, 2, 6, 8};
    private static final int[] EDGES = {1, 3, 5, 7};
    private static final int[][] POSITION_GROUPS = {CENTER, CORNERS, EDGES};
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

    public int chooseMove(EngineGameState gameState, String player) {
        List<String> board = gameState.board();
        String opponent = "X".equals(player) ? "O" : "X";

        OptionalInt winningMove = findCompletingMove(board, player);
        if (winningMove.isPresent()) {
            return winningMove.getAsInt();
        }

        OptionalInt blockingMove = findCompletingMove(board, opponent);
        if (blockingMove.isPresent()) {
            return blockingMove.getAsInt();
        }

        for (int[] positions : POSITION_GROUPS) {
            OptionalInt move = chooseRandomOpenPosition(board, positions);
            if (move.isPresent()) {
                return move.getAsInt();
            }
        }

        throw new IllegalStateException("No legal moves remain.");
    }

    private OptionalInt chooseRandomOpenPosition(List<String> board, int[] positions) {
        List<Integer> openPositions = new ArrayList<>();
        for (int position : positions) {
            if (board.get(position).isBlank()) {
                openPositions.add(position);
            }
        }

        if (openPositions.isEmpty()) {
            return OptionalInt.empty();
        }

        int randomIndex = ThreadLocalRandom.current().nextInt(openPositions.size());
        return OptionalInt.of(openPositions.get(randomIndex));
    }

    private OptionalInt findCompletingMove(List<String> board, String player) {
        for (int[] line : WINNING_LINES) {
            int matchingCells = 0;
            int emptyPosition = -1;

            for (int position : line) {
                String value = board.get(position);
                if (player.equals(value)) {
                    matchingCells++;
                } else if (value.isBlank()) {
                    emptyPosition = position;
                }
            }

            if (matchingCells == 2 && emptyPosition >= 0) {
                return OptionalInt.of(emptyPosition);
            }
        }

        return OptionalInt.empty();
    }
}
