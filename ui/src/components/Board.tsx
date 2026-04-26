import Row from "./Row.tsx";
import styled from "styled-components";

const StyledBoard = styled.div`
  margin-top: 10px;
`

const Board = () => {
  return (
    <StyledBoard>
      <Row/>
      <Row/>
      <Row/>
    </StyledBoard>
  )
}

export default Board