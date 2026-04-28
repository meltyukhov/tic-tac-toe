# Tic-Tac-Toe UI

React/Vite interface for creating an automated Tic Tac Toe session and watching live board updates from the Game Session
Service.

## Run

```bash
npm install
npm run dev
```

The UI expects the Game Session Service at `http://localhost:8080`.

Override that with:

```bash
VITE_SESSION_API_URL=http://localhost:8080 npm run dev
```

## Validate

```bash
npm run build
npm run lint
```
