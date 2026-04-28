package com.example.gamesession.model;

public record SessionEvent(
        String type,
        SessionStatus status,
        EngineGameState gameState,
        MoveEvent move,
        String errorMessage
) {

    public static SessionEvent from(String type, SessionResponse session, MoveRecord moveRecord) {
        return new SessionEvent(
                type,
                session.status(),
                session.gameState(),
                MoveEvent.from(moveRecord),
                session.errorMessage()
        );
    }
}
