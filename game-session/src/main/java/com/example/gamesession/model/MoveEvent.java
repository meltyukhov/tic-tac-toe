package com.example.gamesession.model;

import java.time.Instant;

public record MoveEvent(
        int turn,
        String player,
        int position,
        Instant playedAt
) {

    public static MoveEvent from(MoveRecord moveRecord) {
        if (moveRecord == null) {
            return null;
        }

        return new MoveEvent(
                moveRecord.turn(),
                moveRecord.player(),
                moveRecord.position(),
                moveRecord.playedAt()
        );
    }
}
