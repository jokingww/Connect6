package g04.Group04;

import core.board.Board;
import core.game.Game;
import core.game.Move;
import core.player.AI;

import java.util.Random;

import static core.board.PieceColor.EMPTY;
import static core.game.Move.SIDE;

public class Walking1 extends AI {
    @Override
    public String name() {
        return "AI原始人-走法1";
    }


    /** Return a move for me from the current position, assuming there
     *  is a move. */
    @Override
    public Move findMove(Move opponentMove) {
        if (opponentMove == null) {
            Move move = firstMove();
            board.makeMove(move);
            return move;
        }
        else {
            board.makeMove(opponentMove);
        }

        Random rand = new Random();
        while (true) {
            int index1 = rand.nextInt(SIDE * SIDE);
            int index2 = rand.nextInt(SIDE * SIDE);

            if (index1 != index2 && board.get(index1) == EMPTY && board.get(index2) == EMPTY) {
                Move move = new Move(index1, index2);
                board.makeMove(move);
                return move;
            }
        }
    }

    @Override
    public void playGame(Game game) {
        super.playGame(game);
        board = new Board();
    }
    //使用框架提供的Board
    private Board board = new Board();
}
