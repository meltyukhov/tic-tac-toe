import styled from "styled-components";

type Props = {
  moveCount: number;
};

const Header = styled.div`
  align-items: center;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  padding: 14px 16px;
`;

const Title = styled.h2`
  font-size: 16px;
  margin: 0;
`;

const Count = styled.span`
  color: #475569;
  font-size: 14px;
`;

const MoveHistoryHeader = ({moveCount}: Props) => {
  return (
    <Header>
      <Title>Move History</Title>
      <Count>{moveCount} moves</Count>
    </Header>
  );
};

export default MoveHistoryHeader;
