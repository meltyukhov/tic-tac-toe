import Board from "./Board.tsx";
import Button from "./Button.tsx";
import StatusBox from "./StatusBox.tsx";

const Game = () => {
  return (
    <div>
      <Button>Start Simulation</Button>
      <Board/>
      <StatusBox/>
    </div>
  )
}

export default Game