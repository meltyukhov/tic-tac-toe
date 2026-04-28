# Tic-Tac-Toe

A distributed Tic Tac Toe application where microservices play the game automatically and the UI displays the live board state.

## Components

- `game-engine`: Spring Boot service for board state, move validation, turns, win detection, and draw detection.
- `game-session`: Spring Boot service for session creation, automated move generation, engine coordination, move history, and SSE updates.
- `ui`: React/Vite interface that starts simulations and renders live progress.

## Ports

- Game Engine Service: `http://localhost:8081`
- Game Session Service: `http://localhost:8080`
- UI: `http://localhost:5173`

## Run Locally

Open three terminals from the repository root:

```bash
mvn -f game-engine/pom.xml spring-boot:run
```

```bash
mvn -f game-session/pom.xml spring-boot:run
```

```bash
cd ui
npm install
npm run dev
```

Then open `http://localhost:5173` and click `Start Simulation`.

## Test

Run backend tests from the repository root:

```bash
mvn -f game-engine/pom.xml test
mvn -f game-session/pom.xml test
```

Run UI validation:

```bash
cd ui
npm run build
npm run lint
```

## Game Engine API

`GET /games/{gameId}`

Returns the current board, status, winner, next player, and move count. A game is created lazily if it does not exist.

`POST /games/{gameId}/move`

Request:

```json
{
  "player": "X",
  "position": 0
}
```

Validates turn order and board position, applies the move, and returns the updated game state.

## Game Session API

`POST /sessions`

Creates a new automated game session, initializes the matching engine game, and starts automated play.

`GET /sessions/{sessionId}`

Returns session status, current game state, and move history.

`GET /sessions/{sessionId}/events`

Server-Sent Events stream for live UI updates. Events contain the event type, current status, current board state, and the latest move when one was played.
