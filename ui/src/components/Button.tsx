import type {ReactNode} from "react";
import styled from "styled-components";

type Props = {
  children: ReactNode;
  disabled?: boolean;
  onClick?: () => void;
};

const StyledButton = styled.button`
  align-items: center;
  background: #1658d4;
  border: 0;
  border-radius: 6px;
  color: #fff;
  cursor: pointer;
  display: inline-flex;
  font-family: inherit;
  font-size: 16px;
  font-weight: 700;
  justify-content: center;
  min-height: 42px;
  padding: 0 18px;
  transition: background-color 160ms ease, transform 160ms ease;

  &:hover:not(:disabled) {
    background: #1049b4;
    transform: translateY(-1px);
  }

  &:disabled {
    background: #8ea4ca;
    cursor: progress;
  }
`;

const Button = ({onClick, disabled, children}: Props) => {
  return (
    <StyledButton disabled={disabled} onClick={onClick}>{children}</StyledButton>
  );
};

export default Button;
