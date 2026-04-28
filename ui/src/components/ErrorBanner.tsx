import styled from "styled-components";

type Props = {
  message: string;
};

const Banner = styled.div`
  background: #fff1f2;
  border: 1px solid #fecdd3;
  border-radius: 8px;
  color: #9f1239;
  margin-bottom: 18px;
  padding: 12px 14px;
`;

const ErrorBanner = ({message}: Props) => {
  return <Banner role="alert">{message}</Banner>;
};

export default ErrorBanner;
