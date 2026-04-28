import styled from "styled-components";
import type {SessionResponse} from "../../api";

type Props = {
  session: SessionResponse | null;
};

type Tone = "idle" | "active" | "done" | "error";

const StatusLine = styled.div`
  align-items: center;
  display: flex;
`;

const Dot = styled.span<{$tone: Tone}>`
  background: ${({$tone}) => {
    if ($tone === "active") {
      return "#f2b705";
    }
    if ($tone === "done") {
      return "#28a745";
    }
    if ($tone === "error") {
      return "#d64045";
    }
    return "#94a3b8";
  }};
  border-radius: 999px;
  height: 10px;
  width: 10px;
`;

const Title = styled.div`
  margin-left: 10px;
  color: #111827;
  font-size: 18px;
  font-weight: 800;
`;

const getTone = (session: SessionResponse | null): Tone => {
  if (!session) {
    return "idle";
  }
  if (session.status === "FAILED") {
    return "error";
  }
  if (session.status === "COMPLETED") {
    return "done";
  }
  if (session.status === "IN_PROGRESS") {
    return "active";
  }
  return "idle";
};

const getTitle = (session: SessionResponse | null) => {
  if (!session) {
    return "Ready";
  }
  if (session.status === "FAILED") {
    return "Simulation failed";
  }
  if (session.gameState.status === "WIN") {
    return `${session.gameState.winner} wins`;
  }
  if (session.gameState.status === "DRAW") {
    return "Draw";
  }
  if (session.status === "IN_PROGRESS") {
    return `${session.gameState.nextPlayer}'s turn`;
  }
  return "Session created";
};

const StatusIndicator = ({session}: Props) => {
  return (
    <StatusLine>
      <Dot $tone={getTone(session)}/>
      <Title>{getTitle(session)}</Title>
    </StatusLine>
  );
};

export default StatusIndicator;
