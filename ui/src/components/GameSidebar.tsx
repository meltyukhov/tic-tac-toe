import StatusBox from "./status/StatusBox.tsx";
import MoveHistory from "./history/MoveHistory.tsx";
import styled from "styled-components";
import type {SessionResponse} from "../api";

type Props = {
  session: SessionResponse | null;
};

const Sidebar = styled.aside`
  display: grid;
  gap: 18px;
`;

const GameSidebar = ({session}: Props) => {
  return (
    <Sidebar>
      <StatusBox session={session}/>
      <MoveHistory moves={session?.moveHistory ?? []}/>
    </Sidebar>
  );
};

export default GameSidebar;