import type {ReactNode} from "react";
import styled from "styled-components";

type Props = {
  board: ReactNode;
  feedback?: ReactNode;
  header: ReactNode;
  sidebar: ReactNode;
};

const Page = styled.main`
  color: #111827;
  margin: 0 auto;
  max-width: 1080px;
  padding: 40px 20px;
`;

const Layout = styled.div`
  align-items: start;
  display: grid;
  gap: 24px;
  grid-template-columns: minmax(320px, 0.9fr) minmax(280px, 1.1fr);

  @media (max-width: 820px) {
    grid-template-columns: 1fr;
  }
`;

const GameLayout = ({board, feedback, header, sidebar}: Props) => {
  return (
    <Page>
      {header}
      {feedback}
      <Layout>
        {board}
        {sidebar}
      </Layout>
    </Page>
  );
};

export default GameLayout;
