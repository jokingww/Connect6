package test02.player;

import core.game.Game;
import core.game.Move;

import java.util.Random;

import static core.board.PieceColor.EMPTY;
import static core.game.Move.SIDE;

public class AI extends core.player.AI {
    @Override
    public String name() {
        return "不二";
    }

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
        board = new G02Board();
    }

    //使用我自己写的Board
    private G02Board board = new G02Board();
}
