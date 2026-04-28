import styled from "styled-components";
import Button from "./Button.tsx";

type Props = {
  isRunning: boolean;
  onStartSimulation: () => void;
};

const Header = styled.header`
  align-items: center;
  display: flex;
  gap: 18px;
  justify-content: space-between;
  margin-bottom: 28px;

  @media (max-width: 680px) {
    align-items: stretch;
    flex-direction: column;
  }
`;

const HeadingGroup = styled.div`
  display: grid;
  gap: 6px;
`;

const Title = styled.h1`
  font-size: clamp(30px, 5vw, 52px);
  line-height: 1;
  margin: 0;
`;

const Subtitle = styled.p`
  color: #64748b;
  font-size: 16px;
  margin: 0;
`;

const GameHeader = ({isRunning, onStartSimulation}: Props) => {
  return (
    <Header>
      <HeadingGroup>
        <Title>Tic Tac Toe</Title>
        <Subtitle>Automated microservice match</Subtitle>
      </HeadingGroup>
      <Button disabled={isRunning} onClick={onStartSimulation}>
        {isRunning ? "Simulating" : "Start Simulation"}
      </Button>
    </Header>
  );
};

export default GameHeader;
