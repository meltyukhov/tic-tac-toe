import styled from "styled-components";
import type {SessionResponse} from "../../api";

type Props = {
  session: SessionResponse | null;
};

const Details = styled.div`
  margin-top: 14px;
  color: #475569;
  display: grid;
  font-size: 14px;
  gap: 6px;
`;

const StatusDetails = ({session}: Props) => {
  return (
    <Details>
      <span>Session: {session?.sessionId ?? "None"}</span>
      <span>Moves: {session?.gameState.movesPlayed ?? 0} / 9</span>
      <span>Game status: {session?.gameState.status ?? "Not started"}</span>
    </Details>
  );
};

export default StatusDetails;
