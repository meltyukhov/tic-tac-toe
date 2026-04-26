import type {ReactNode} from "react";
import styled from "styled-components";

type Props = {
  children: ReactNode
}

const StyledButton = styled.button`
    color: #fff;
    background-color: #195de6;
    border-style: none;
    border-radius: 3px;
    font-family: Avenir, Helvetica, Arial, sans-serif;
    font-size: 16px;
    padding: 3px 10px;
    cursor: pointer;
`

const Button = ({children}: Props) => {
  return (
   <StyledButton>{children}</StyledButton>
  )
}

export default Button