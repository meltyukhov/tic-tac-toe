import styled from "styled-components"
import {useState} from "react";
import {CELL_SIZE} from "./constants.ts";

const StyledSquare = styled.div`
    border: 1px solid #000;
    margin-left: -1px;
    width: ${CELL_SIZE}px;
    height: ${CELL_SIZE}px;
    display: inline-flex;
    justify-content: center;
    align-items: center;
    cursor: default;
    user-select: none;
    vertical-align: top;
    
    font-size: 2em;
    font-weight: bold;
`

const Square = () => {
  const [isChecked, setIsChecked] = useState(false)

  const toggle = () => {
    setIsChecked(!isChecked)
  }

  return (
    <StyledSquare onClick={toggle}>{isChecked && "X"}</StyledSquare>
  )
}

export default Square