import Square from "./Square.tsx";
import styled from "styled-components";

const StyledRow = styled.div`
    margin-top: -1px;
`

const Row = () => {
  return (
    <StyledRow>
      <Square/>
      <Square/>
      <Square/>
    </StyledRow>
  )
}

export default Row