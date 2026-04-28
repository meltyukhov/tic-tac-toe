# Game Session Service

Manages automated Tic Tac Toe sessions, chooses legal moves for both players, forwards them to the Game Engine Service, stores move history in memory, and publishes live Server-Sent Events for the UI. Creating a session starts automated play; there is no separate simulation trigger endpoint.

## Run

Start the Game Engine Service first on `http://localhost:8081`, then run:

```bash
mvn -f game-session/pom.xml spring-boot:run
```

The service runs on `http://localhost:8080`.

## Configuration

- `game-engine.base-url`: Game Engine Service URL. Defaults to `http://localhost:8081`.
- `simulation.move-delay-ms`: Delay between automated moves for visible UI progress. Defaults to `500`.
