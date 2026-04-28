import styled from "styled-components";

type Props = {
  index: number;
  value: string;
  isWinningCell?: boolean;
};

const StyledSquare = styled.div<{$isWinningCell: boolean; $value: string}>`
  align-items: center;
  aspect-ratio: 1;
  background: ${({$isWinningCell}) => $isWinningCell ? "#e7f7ec" : "#f8fafc"};
  border: 1px solid ${({$isWinningCell}) => $isWinningCell ? "#28a745" : "#cbd5e1"};
  color: ${({$value}) => $value === "X" ? "#d64045" : "#155e75"};
  display: flex;
  font-size: 42px;
  font-weight: 900;
  justify-content: center;
  min-height: 92px;
  min-width: 92px;
  user-select: none;
`;

const Square = ({index, value, isWinningCell = false}: Props) => {
  return (
    <StyledSquare
      aria-label={`Cell ${index + 1}${value ? ` ${value}` : " empty"}`}
      $isWinningCell={isWinningCell}
      $value={value}
    >
      {value}
    </StyledSquare>
  );
};

export default Square;
