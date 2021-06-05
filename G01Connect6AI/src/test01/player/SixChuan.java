package test01.player;

import core.board.Board;
import core.game.Game;
import core.game.Move;
import core.player.AI;

import java.util.Random;

import static core.board.PieceColor.EMPTY;
import static core.game.Move.SIDE;

public class SixChuan extends AI {


    /** Return a move for me from the current position, assuming there
     *  is a move. */
//    @Override
//    public Move findMove(Move opponentMove) {
//        if (opponentMove == null) {
//            Move move = firstMove();
//            board.makeMove(move);
//            return move;
//        }
//        else {
//            board.makeMove(opponentMove);
//        }
//
//        Random rand = new Random();
//        while (true) {
//            int index1 = rand.nextInt(SIDE * SIDE);
//            int index2 = rand.nextInt(SIDE * SIDE);
//
//            if (index1 != index2 && board.get(index1) == EMPTY && board.get(index2) == EMPTY) {
//                Move move = new Move(index1, index2);
//                board.makeMove(move);
//                return move;
//            }
//        }
//    }
//
//    @Override
//    public String name() {
//        // TODO Auto-generated method stub
//        return "G01-SixKiller";  //组编号-为自己的AI所取的名字
//    }
//
//    Board board = new Board();
//
//    /* (non-Javadoc)
//     * @see core.player.Player#setBoard(core.board.Board)
//     */
//
//    public Board setBoard(Board board) {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    /* (non-Javadoc)
//     * @see core.player.Player#getBoard()
//     */
//
//    public Board getBoard() {
//        // TODO Auto-generated method stub
//        return null;
//    }
    @Override
    public String name() {
        return "我爱六个串";
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
