package g04.Group04;

import core.board.Board;
import core.game.Game;
import core.game.Move;
import core.player.AI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import static core.board.PieceColor.EMPTY;
import static core.game.Move.SIDE;

public class Walking2 extends AI {
    @Override
    public String name() {
        return "AI原始人-走法2";
    }


/** Return a move for me from the current position, assuming there
 *  is a move. */
    @Override
    public Move findMove(Move opponentMove) {
        if (opponentMove == null) {
            Move move = firstMove();
            board.makeMove(move);
            return move;
        } else {
            board.makeMove(opponentMove);
        }

        Random rand = new Random();
        // 随机生成第一个子的位置
        int index1;
        do {
            index1 = rand.nextInt(SIDE * SIDE);
        } while (board.get(index1) != EMPTY);

        // 生成第二个子的位置
        ArrayList<Integer> pos = new ArrayList<Integer>(Arrays.asList(index1-SIDE-1, index1-SIDE, index1-SIDE+1, index1-1, index1+1, index1+SIDE-1, index1+SIDE, index1+SIDE+1));
        for (Iterator<Integer> ite = pos.iterator(); ite.hasNext();) {
            Integer p = ite.next();
            if (!Move.validSquare(p)) {
                ite.remove();
            }
        }
        int num = pos.size();
        int index2 = rand.nextInt(num);
        while (num > 1 && board.get(pos.get(index2)) != EMPTY) {
            pos.remove(index2);
            num--;
            index2 = rand.nextInt(num);
        }
        if (num <= 1 && board.get(pos.get(index2)) != EMPTY) {
            do {
                index2 = rand.nextInt(SIDE * SIDE);
            } while (index1 == index2 || board.get(index2) != EMPTY);
        } else {
            index2 = pos.get(index2);
        }

        Move move = new Move(index1, index2);
        board.makeMove(move);
        return move;
    }


    @Override
    public void playGame(Game game) {
        super.playGame(game);
        board = new Board();
    }

    private Board board = new Board();
}
