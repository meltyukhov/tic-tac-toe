import styled from "styled-components";
import type {MoveRecord} from "../../api";
import MoveHistoryItem from "./MoveHistoryItem.tsx";

type Props = {
  moves: MoveRecord[];
};

const List = styled.ol`
    list-style: none;
    margin: 0;
    padding: 0;
`;

const MoveHistoryList = ({moves}: Props) => {
  return (
    <List>
      {moves.map((move) => (
        <MoveHistoryItem key={move.turn} move={move}/>
      ))}
    </List>
  );
};

export default MoveHistoryList;
