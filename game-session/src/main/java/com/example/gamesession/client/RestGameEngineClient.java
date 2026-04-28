package com.example.gamesession.client;

import com.example.gamesession.exception.EngineCommunicationException;
import com.example.gamesession.model.EngineGameState;
import com.example.gamesession.model.GameMove;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class RestGameEngineClient implements EngineClient {

    private final RestClient restClient;

    public RestGameEngineClient(@Value("${game-engine.base-url}") String engineBaseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(engineBaseUrl)
                .build();
    }

    @Override
    public EngineGameState getGame(String gameId) {
        try {
            EngineGameState state = restClient.get()
                    .uri("/games/{gameId}", gameId)
                    .retrieve()
                    .body(EngineGameState.class);
            return requireBody(state);
        } catch (RestClientResponseException exception) {
            throw new EngineCommunicationException("Game engine rejected the game lookup: "
                    + exception.getResponseBodyAsString(), exception);
        } catch (RestClientException exception) {
            throw new EngineCommunicationException("Unable to contact the game engine.", exception);
        }
    }

    @Override
    public EngineGameState submitMove(String gameId, String player, int position) {
        try {
            EngineGameState state = restClient.post()
                    .uri("/games/{gameId}/move", gameId)
                    .body(new GameMove(player, position))
                    .retrieve()
                    .body(EngineGameState.class);
            return requireBody(state);
        } catch (RestClientResponseException exception) {
            throw new EngineCommunicationException("Game engine rejected the move: "
                    + exception.getResponseBodyAsString(), exception);
        } catch (RestClientException exception) {
            throw new EngineCommunicationException("Unable to contact the game engine.", exception);
        }
    }

    private EngineGameState requireBody(EngineGameState state) {
        if (state == null) {
            throw new EngineCommunicationException("Game engine returned an empty response.");
        }
        return state;
    }
}
