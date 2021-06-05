package g04.Group04;

import static core.board.PieceColor.*;

import core.board.PieceColor;
import core.game.Game;
import core.game.Move;

import java.util.*;

public class AI extends core.player.AI {
    @Override
    public String name() {
        return name;
    }

    public AI(String name) {
        this.name = name;
    }

    public AI() {
        this.name = "Group04";
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    @Override
    public Move findMove(Move opponentMove) {
        if (opponentMove == null) {
            if (player == EMPTY) {
                player = WHITE;
                board.roadTable.setScore(player);
            }
            Move move = firstMove();
            board.doMakeMove(move);
            steps++;
            return move;
        }
        else {
            if (player == EMPTY) player = BLACK;
            showMove(opponentMove);
            board.doMakeMove(opponentMove);
        }
        if (board.getWhoseMove() == BLACK) {
            if (steps == 0 && slash()) {
                showMove(bestMove);
                board.doMakeMove(bestMove);
                steps++;
                return bestMove;
            }
        }
        System.out.println("current player:" + board.getWhoseMove());
        // 如果TSS失败，执行alpha-beta剪枝
        // if (!threatSpaceSearch(tssDepth, 0)) {
        //    System.out.println("alpha-beta");
        //    alphaBeta(-Integer.MAX_VALUE, Integer.MAX_VALUE, maxDepth);
        // }
        bestMove = null;
        for (int i = 0; i < tssDepth; i+= 2) {
            if (threatSpaceSearch(i, 0))
                break;
        }
        if (bestMove == null) {
            System.out.println("alpha-beta");
            alphaBeta(-Integer.MAX_VALUE, Integer.MAX_VALUE, maxDepth);
        }
        showMove(bestMove);
        board.doMakeMove(bestMove);
        steps++;
        return bestMove;
    }

    /**
     * alpha-beta方法采用α-β剪枝进行递归搜索(采用负极大值方法)
     */
    public int alphaBeta(int alpha, int beta, int depth){
        int value, best = -Integer.MAX_VALUE;
        //如果棋局结束或当前节点为叶子节点则返回评估值
        if (board.gameOver() || depth == 0) {
            return board.roadTable.evaluation(board);
        }
        // 如果在置换表当中，且深度小于表中深度
        // if (transTable.containsKey(board.hash) && transTable.get(board.hash).getDepth() >= depth) {
        //   return transTable.get(board.hash).getEvaluation();
        // }
        ArrayList<Move> moves = getMoves(depth);
        for (Move move : moves) {
            board.makeMove(move);
            // 负极大值方法
            value  = -alphaBeta(-beta, -alpha,depth - 1);
            board.unMakeMove(move);
            if (value > best) {
                best = value;
                if (best > alpha) {
                    alpha = best;
                }
                if (value >= beta) {
                    break;
                }
            }
            // 如果是第一层则记录最优Move
            if (depth == maxDepth && value >= best){
                bestMove = move;
            }
        }
        // transTable.put(board.hash, new Node(depth, best));
        return best;
    }

    /**
     * alpha-beta方法采用α-β剪枝进行递归搜索(采用负极大值方法)
     */
    public int pvs(int alpha, int beta, int depth){
        //如果棋局结束或当前节点为叶子节点则返回评估值
        if (depth == 0){
            return board.roadTable.evaluation(board);
        }
        int tmp = -Integer.MAX_VALUE;
        boolean flag = false;
        ArrayList<Move> moves = getMoves(depth);
        int n = Math.min(moves.size(), 100);
        if (depth == maxDepth)
            bestMove = moves.get(0);
        for (int i = 0; i < n; i++) {
            board.makeMove(moves.get(i));
            if (flag) {
                tmp = -pvs(-alpha - 1, -alpha, depth - 1);
                if (tmp > alpha && tmp < beta) {
                    tmp = -pvs(-beta, -alpha, depth - 1);
                }
            } else {
                tmp = -pvs(-beta, -alpha, depth - 1);
            }
            board.unMakeMove(moves.get(i));
            if (tmp >= beta)
                return beta;
            if (tmp > alpha) {
                alpha = tmp;
                flag = true;
                if (depth == maxDepth) {
                    board.makeMove(moves.get(i));
                    if (!threatSpaceSearch(tssDepth, 0)) {
                        bestMove = moves.get(i);
                    }
                    board.unMakeMove(moves.get(i));
                }
            }
        }
        return alpha;
    }

    /**
     * 威胁空间搜索
     */
    public boolean threatSpaceSearch(int depth, int layer) {
        if (board.roadTable.getRoadsByNum(6, player).size() > 0) {
            return true;
        }
        PieceColor opponent = player.opposite();
        // 我方走步时，对方存在威胁，但是我方没有威胁
        if (board.getWhoseMove() == player && threatsNum(opponent) > 0 && threatsNum(player) == 0)
            return false;
        // 对方走步时，我方威胁大于3
        if (board.getWhoseMove() == opponent && threatsNum(player) >= 3)
            return true;
        if (depth == 0) return false;
        ArrayList<Move> moves = getThreateMoves();
        for (Move move : moves) {
            board.makeMove(move);
            boolean winThreat = threatSpaceSearch(depth - 1, layer + 1);
            board.unMakeMove(move);
            if (board.getWhoseMove() == player) {
                if (winThreat) {
                    if (layer == 0)
                        bestMove = move;
                    return true;
                }
            }
            else {
                if (!winThreat) return false;
            }
        }
        return board.getWhoseMove() != player;
        //return false;
    }

    // alpha-beta剪枝获取当前走步
    public ArrayList<Move> getMoves(int depth) {
        ArrayList<Move> moves = findWinMove();
        if (moves == null || moves.size() == 0) {
            // 若没有找到必胜走法，计算对手的威胁数
            int threats = threatsNum(board.getWhoseMove().opposite());
            if (threats >= 2) // 双威胁防守策略
                moves = defendMove();
            else if (threats == 1) // 单威胁防守策略
                moves = defendOneMove();
            else // 最佳评估着法
                moves = board.getTopMoves(11, 7);
        }
        return moves;
    }

    // 威胁空间搜索获取当前走步
    public ArrayList<Move> getThreateMoves() {
        ArrayList<Move> moves = findWinMove();
        if (moves == null || moves.size() == 0) {
            // 对方存在威胁，进行防守
            if (threats(board.getWhoseMove().opposite()) != 0)
                moves = defendMove();
            else // 对方无威胁，生成己方威胁
                moves = generateThreats();
        }
        return moves;
    }

    public ArrayList<Move> findWinMove() {
        HashMap<Integer, Road> four, five;
        four = board.roadTable.getRoadsByNum(4, board.getWhoseMove());
        five = board.roadTable.getRoadsByNum(5, board.getWhoseMove());
        ArrayList<Move> moves = new ArrayList<>();
        // 四子威胁
        if (four.size() > 0) {
            Road road = four.values().iterator().next();
            List<Point> points = board.findEmptyPoints(road);
            moves.add(new Move(points.get(0).getIndex(), points.get(1).getIndex()));
        }
        // 五子威胁
        else if (five.size() > 0) {
            Road road = five.values().iterator().next();
            Point one = board.findEmptyPoints(road).get(0);
            Point another = board.getRandPoint();
            while (one.equals(another)) {
                another = board.getRandPoint();
            }
            moves.add(new Move(one.getIndex(), another.getIndex()));
        }
        return moves;
    }

    // 对对方四五子路防守
    public ArrayList<Move> defendMove() {
        HashMap<Integer, Road> four, five;
        PieceColor opponent = board.getWhoseMove().opposite();
        four = board.roadTable.getRoadsByNum(4, opponent);
        five = board.roadTable.getRoadsByNum(5, opponent);
        if (four.size() == 0 && five.size() == 0)
            return null;
        HashSet<Integer> emptyPointSet = new HashSet<>();
        // 4,5路上的空白位置
        ArrayList<Point> emptyPoints = emptyPoints(emptyPointSet, four);
        emptyPoints.addAll(emptyPoints(emptyPointSet, five));
        ArrayList<Move> moves = new ArrayList<>();
        for (int i = 0; i < emptyPoints.size(); i++) {
            Point one = emptyPoints.get(i);
            board.makeStep(one);
            for (int j = i + 1; j < emptyPoints.size(); j++) {
                Point another = emptyPoints.get(j);
                board.makeStep(another);
                // 威胁消除
                if (threats(opponent) == 0) {
                    moves.add(new Move(one.getIndex(), another.getIndex()));
                }
                board.unMakeStep(another);
            }
            board.unMakeStep(one);
        }
        if (moves.size() == 0)
            return board.getTopMoves(1, 1);
        return moves;
    }

    // 对对方四五子路防守
    public ArrayList<Move> defendOneMove() {
        HashMap<Integer, Road> four, five;
        PieceColor opponent = board.getWhoseMove().opposite();
        four = board.roadTable.getRoadsByNum(4, opponent);
        five = board.roadTable.getRoadsByNum(5, opponent);
        HashSet<Integer> emptyPointSet = new HashSet<>();
        ArrayList<Point> emptyPoints = emptyPoints(emptyPointSet, four);
        emptyPoints.addAll(emptyPoints(emptyPointSet, five));
        ArrayList<Move> moves = new ArrayList<>();
        for (Point one : emptyPoints) {
            board.makeStep(one);
            // 如果落子后威胁消除
            if (threats(opponent) == 0) {
                // 第二个子为当前评分最高的num个子
                for (Point another : board.getTopPoint(5)) {
                    moves.add(new Move(one.getIndex(), another.getIndex()));
                }
            }
            board.unMakeStep(one);
        }
        return board.sortMoveHeuristic(moves);
    }

    // 威胁数
    public int threatsNum(PieceColor player) {
        HashMap<Integer, Road> four, five, temp;
        PieceColor opponent = player.opposite();
        four = board.roadTable.getRoadsByNum(4, player);
        five = board.roadTable.getRoadsByNum(5, player);
        if (four.size() == 0 && five.size() == 0)
            return 0;
        temp = (four.size() == 0) ? five : four;
        Road road = temp.values().iterator().next();
        // 单落一子是否威胁消失
        for (Point point: board.findEmptyPoints(road)) {
            board.makeStep(point, opponent);
            int threats = threats(player);
            board.unMakeStep(point, opponent);
            if (threats == 0) return 1;
        }
        // 获取4，5路中的空白位置
        HashSet<Integer> emptyPointSet = new HashSet<>();
        ArrayList<Point> emptyPoints = emptyPoints(emptyPointSet, four);
        emptyPoints.addAll(emptyPoints(emptyPointSet, five));
        boolean flag = false;
        for (int i = 0; i < emptyPoints.size(); i++) {
            Point one = emptyPoints.get(i);
            board.makeStep(one, opponent);
            for (int j = i + 1; j < emptyPoints.size(); j++) {
                Point another = emptyPoints.get(j);
                board.makeStep(another, opponent);
                // 落两子后威胁消失
                if (threats(player) == 0)
                    flag = true;
                board.unMakeStep(another, opponent);
            }
            board.unMakeStep(one, opponent);
        }
        return flag ? 2 : 3;
    }

    // 是否有威胁存在
    public int threats(PieceColor player) {
        return board.roadTable.getRoadsByNum(4, player).size() + board.roadTable.getRoadsByNum(5, player).size();
    }

    public boolean hasTwoThreats(PieceColor player) {
        HashMap<Integer, Road> four, five, temp;
        PieceColor opponent = player.opposite();
        four = board.roadTable.getRoadsByNum(4, player);
        five = board.roadTable.getRoadsByNum(5, player);
        if (four.size() == 0 && five.size() == 0)
            return false;
        temp = (four.size() == 0) ? five : four;
        Road road = temp.values().iterator().next();
        for (Point point: board.findEmptyPoints(road)) {
            board.makeStep(point, opponent);
            int threats = threats(player);
            board.unMakeStep(point, opponent);
            if (threats == 0) return false;
        }
        return true;
    }

    // 生成威胁走法
    public ArrayList<Move> generateThreats() {
        HashMap<Integer, Road> two, three;
        two = board.roadTable.getRoadsByNum(2, board.getWhoseMove());
        three = board.roadTable.getRoadsByNum(3, board.getWhoseMove());
        HashSet<Integer> emptyPointSet = new HashSet<>();
        ArrayList<Point> emptyPoints = emptyPoints(emptyPointSet, two);
        emptyPoints.addAll(emptyPoints(emptyPointSet, three));
        ArrayList<Move> moves1 = new ArrayList<>();
        ArrayList<Move> moves2 = new ArrayList<>();
        for (int i = 0; i < emptyPoints.size(); i++) {
            Point one = emptyPoints.get(i);
            board.makeStep(one);
            for (int j = i + 1; j < emptyPoints.size(); j++) {
                Point another = emptyPoints.get(j);
                board.makeStep(another);
                int threats = threatsNum(board.getWhoseMove());
                if (threats == 2) {
                    moves1.add(new Move(one.getIndex(), another.getIndex()));
                } else if (threats == 3) {
                    moves2.add(new Move(one.getIndex(), another.getIndex()));
                }
                board.unMakeStep(another);
            }
            board.unMakeStep(one);
        }
        if (moves2.size() != 0)
            return moves2;
        else
            return board.sortMoveHeuristic(moves1);
    }

    // 路中的空白子
    public ArrayList<Point> emptyPoints(HashSet<Integer> emptyPointSet, HashMap<Integer, Road> one) {
        ArrayList<Point> emptyPoints = new ArrayList<>();
        for (Road road: one.values()) {
            List<Point> points = board.findEmptyPoints(road);
            for (Point p: points) {
                if (!emptyPointSet.contains(p.getIndex())) {
                    emptyPointSet.add(p.getIndex());
                    emptyPoints.add(p);
                }
            }
        }
        return emptyPoints;
    }

    @Override
    public void playGame(Game game) {
        super.playGame(game);
        // board = new MyBoard();
    }

    public void showMove(Move move) {
        int row0 = move.row0() - 'A';
        int col0 = move.col0() - 'A';
        int row1 = move.row1() - 'A';
        int col1 = move.col1() - 'A';
        System.out.println("step1: " + row0 + ", " + col0
                + " step2: "+ row1 + ", " + col1 + " score: " + board.roadTable.evaluation(board));
    }

    // 白棋首步随机下法
    public Move firstMove() {
        Random random = new Random();
        int i = random.nextInt(6);
        return new Move(Point.index(first[i][0], first[i][1]), Point.index(first[i][2], first[i][3]));
    }

    // 开局斜线下法
    public boolean slash() {
        ArrayList<Move> moves = new ArrayList<>();
        if (board.get(8, 8) == EMPTY && board.get(10, 10) == EMPTY && board.get(7, 7) == EMPTY && board.get(11, 11) == EMPTY) {
            moves.add(new Move(Point.index(8, 8), Point.index(10, 10)));
        }
        if (board.get(8, 10) == EMPTY && board.get(10, 8) == EMPTY && board.get(7, 11) == EMPTY && board.get(11, 7) == EMPTY) {
            moves.add(new Move(Point.index(8, 10), Point.index(10, 8)));
        }
        // if (board.get(8, 10) == WHITE && board.get(10, 10) == WHITE) {
        //     moves.add(new Move(Point.index(8, 8), Point.index(7, 7)));
        // }
        // if (board.get(8, 8) == WHITE && board.get(10, 8) == WHITE) {
        //     moves.add(new Move(Point.index(8, 10), Point.index(7, 11)));
        // }
        // if (board.get(8, 10) == WHITE && board.get(8, 10) == WHITE) {
        //     moves.add(new Move(Point.index(10, 10), Point.index(11, 11)));
        // }
        // if (board.get(10, 8) == WHITE && board.get(10, 10) == WHITE) {
        //     moves.add(new Move(Point.index(8, 10), Point.index(7, 11)));
        // }
        if (moves.size() != 0) {
            moves = board.sortMoveHeuristic(moves);
            bestMove = moves.get(0);
            System.out.println("threat true");
            startThreat = true;
            return true;
        }
        return false;
    }

    // 开局连4威胁生成算法
    public boolean startThreatThree() {
        HashMap<Integer, Road> three;
        three = board.roadTable.getRoadsByNum(3, board.getWhoseMove());
        Point one = board.findConnectPoints(three.values().iterator().next());
        if (one == null)
            return false;
        System.out.println("threat: " + one);
        board.makeStep(one);
        Point another = board.getTopPoint(1).get(0);
        board.unMakeStep(one);
        bestMove = new Move(one.getIndex(), another.getIndex());
        startThreat = false;
        return true;
    }

    //六子棋棋盘
    private MyBoard board = new MyBoard();
    // 黑白方
    private PieceColor player = EMPTY;
    // alpha-beta剪枝最大深度
    private int maxDepth = 4;
    // tss搜索最大深度
    private int tssDepth = 9;
    // 记录最佳走步
    private Move bestMove = null;
    // 走过的步数
    private int steps = 0;
    // 棋手名
    private String name;
    // 开局连四威胁
    private boolean startThreat = false;
    // 开局走步
    private int[][] first = new int[][] {{8, 11, 7, 10}, {10, 7, 11, 8}, {10, 11, 11, 10}, {7, 8, 8, 7}, {7, 10, 8, 11}, {11, 8, 10, 7},};
    // 置换表
    // private HashMap<Integer, Node> transTable = new HashMap<>();
}
