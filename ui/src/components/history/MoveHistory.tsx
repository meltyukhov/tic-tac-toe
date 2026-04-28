import styled from "styled-components";
import type {MoveRecord} from "../../api";
import MoveHistoryHeader from "./MoveHistoryHeader.tsx";
import MoveHistoryList from "./MoveHistoryList.tsx";

type Props = {
  moves: MoveRecord[];
};

const MoveLog = styled.section`
  border: 1px solid #d8dee9;
  border-radius: 8px;
  max-height: 364px;
  overflow: auto;
`;

const EmptyState = styled.div`
  color: #64748b;
  padding: 18px 16px;
`;

const MoveHistory = ({moves}: Props) => {
  return (
    <MoveLog>
      <MoveHistoryHeader moveCount={moves.length}/>
      {moves.length ? <MoveHistoryList moves={moves}/> : <EmptyState>No moves yet.</EmptyState>}
    </MoveLog>
  );
};

export default MoveHistory;
