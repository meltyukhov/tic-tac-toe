package com.example.gamesession.client;

import com.example.gamesession.model.EngineGameState;

public interface EngineClient {

    EngineGameState getGame(String gameId);

    EngineGameState submitMove(String gameId, String player, int position);
}
