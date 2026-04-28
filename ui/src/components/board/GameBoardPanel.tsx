import styled from "styled-components";
import Board from "./Board.tsx";

type Props = {
  board: string[];
  winningCells: number[];
};

const BoardArea = styled.section`
  background: #ffffff;
  border: 1px solid #d8dee9;
  border-radius: 8px;
  display: grid;
  place-items: center;
  padding: 24px;
`;

const GameBoardPanel = ({board, winningCells}: Props) => {
  return (
    <BoardArea>
      <Board board={board} winningCells={winningCells}/>
    </BoardArea>
  );
};

export default GameBoardPanel;
