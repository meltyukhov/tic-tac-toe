package com.example.gameengine.controller;

import com.example.gameengine.model.GameState;
import com.example.gameengine.model.MoveRequest;
import com.example.gameengine.service.GameEngineService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
@CrossOrigin(origins = "*")
public class GameController {

    private final GameEngineService gameEngineService;

    public GameController(GameEngineService gameEngineService) {
        this.gameEngineService = gameEngineService;
    }

    @GetMapping("/{gameId}")
    public GameState getGame(@PathVariable String gameId) {
        return gameEngineService.getGame(gameId);
    }

    @PostMapping("/{gameId}/move")
    public GameState makeMove(@PathVariable String gameId, @RequestBody MoveRequest request) {
        return gameEngineService.makeMove(gameId, request);
    }
}
