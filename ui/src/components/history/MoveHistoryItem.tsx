import styled from "styled-components";
import type {MoveRecord} from "../../api";

type Props = {
  move: MoveRecord;
};

const Item = styled.li`
  align-items: center;
  border-bottom: 1px solid #edf2f7;
  display: grid;
  gap: 10px;
  grid-template-columns: 42px 1fr auto;
  min-height: 48px;
  padding: 10px 16px;

  &:last-child {
    border-bottom: 0;
  }
`;

const TurnBadge = styled.span<{$player: string}>`
  align-items: center;
  background: ${({$player}) => $player === "X" ? "#fee2e2" : "#cffafe"};
  border-radius: 999px;
  color: ${({$player}) => $player === "X" ? "#991b1b" : "#155e75"};
  display: inline-flex;
  font-weight: 800;
  height: 28px;
  justify-content: center;
  width: 28px;
`;

const CellText = styled.span`
  color: #475569;
  font-size: 14px;
`;

const MoveHistoryItem = ({move}: Props) => {
  return (
    <Item>
      <TurnBadge $player={move.player}>{move.player}</TurnBadge>
      <span>Turn {move.turn}</span>
      <CellText>Cell {move.position + 1}</CellText>
    </Item>
  );
};

export default MoveHistoryItem;
