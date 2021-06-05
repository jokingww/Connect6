package test02.player;

import core.board.PieceColor;
import core.game.Move;

import static core.board.PieceColor.*;
import static core.game.Move.*;

public class G02Board {

    /** A new, cleared board at the start of the game. */
    public G02Board() {
        _board = new PieceColor[SIDE * SIDE];
        clear();
    }

    /** Clear me to my starting state, with pieces in their initial positions. */
    public void clear() {
        _whoseMove = WHITE;

        for (int i = 0; i < SIDE * SIDE; i++) {
            _board[i] = EMPTY;
        }
        //黑方先下第一个子，默认为棋盘的天元
        _board[index('J', 'J')] = BLACK;
    }

    /** Return the current contents of the square at linearized index K. */
    public PieceColor get(int k) {
        return _board[k];
    }

    /** Set square(C, R) to V, where 'A' <= C <= 'S', and 'A' <= R <= 'S'. */
    protected void set(char c, char r, PieceColor v) {
        set(index(c, r), v);
    }

    /** Set square(K) to V, where K is the linearized index of a square. */
    protected void set(int k, PieceColor v) {
        _board[k] = v;
    }

    /** Make the Move MOV on this Board, assuming it is legal. */
    public void makeMove(Move mov) {

        //System.out.println(_whoseMove + ": " + "move " + mov.toString());
        set(mov.col0(), mov.row0(), _whoseMove);
        set(mov.col1(), mov.row1(), _whoseMove);

        _whoseMove = _whoseMove.opposite();
    }

    /** Player that is on move. */
    private PieceColor _whoseMove;

    /** Player that is on move. */
    private final PieceColor[] _board;
}
