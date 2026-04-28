import styled from "styled-components";
import type {SessionResponse} from "../../api";
import StatusDetails from "./StatusDetails.tsx";
import StatusIndicator from "./StatusIndicator.tsx";

type Props = {
  session: SessionResponse | null;
};

const Wrapper = styled.section`
  border: 1px solid #d8dee9;
  border-radius: 8px;
  padding: 18px;
`;

const StatusBox = ({session}: Props) => {
  return (
    <Wrapper>
      <StatusIndicator session={session}/>
      <StatusDetails session={session}/>
    </Wrapper>
  );
};

export default StatusBox;
