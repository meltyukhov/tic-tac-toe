import styled from "styled-components";
import Square from "./Square.tsx";

const StyledBoard = styled.div`
  display: grid;
  grid-template-columns: repeat(3, minmax(74px, 1fr));
  gap: 8px;
  max-width: 316px;
  width: min(100%, 316px);
`;

type Props = {
  board: string[];
  winningCells?: number[];
};

const Board = ({board, winningCells = []}: Props) => {
  return (
    <StyledBoard aria-label="Tic Tac Toe board">
      {board.map((value, index) => (
        <Square
          index={index}
          isWinningCell={winningCells.includes(index)}
          key={index}
          value={value}
        />
      ))}
    </StyledBoard>
  );
};

export default Board;
