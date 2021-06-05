package g04.Group04;

import core.board.PieceColor;
import core.game.Move;

import java.util.*;

import static core.board.PieceColor.*;
import static core.game.Move.SIDE;

public class MyBoard {

    // 游戏棋盘
    public MyBoard() {
        _board = new PieceColor[SIDE * SIDE];
        clear();
    }

    // 重置棋盘
    public void clear() {
        _whoseMove = WHITE;
        for (int i = 0; i < SIDE * SIDE; i++) {
            _board[i] = EMPTY;
        }
        this.moveList.clear();
        Arrays.fill(_center, 0);
        for (int r = -2; r <= 2; r++) {
            for (int c = -2; c <= 2; c++) {
                _center[index(r + 9, c + 9)]++;
            }
        }
        // 初始化zobrist数组
        // for(int r = 0; r < 19; r++) {
        //     for(int c = 0; c < 19; c++) {
        //         for(int k = 0; k < 3; k++) {
        //             zobrist[index(r, c)][k] = rand.nextInt();
        //         }
        //     }
        // }
        // 黑方先下第一个子，默认为棋盘的天元
        _board[index(9, 9)] = BLACK;
        roadTable.makeChess(9, 9, BLACK);
        // hash ^= zobrist[index(9, 9)][1];
        System.out.println("eva: "  + roadTable.evaluation(this));
    }

    // 获取棋子位置处颜色
    public PieceColor get(int k) {
        return _board[k];
    }

    // 获取棋子位置处颜色
    public PieceColor get(int r, int c) {
        return _board[r * 19 + c];
    }

    // 在位置坐标处落子
    protected void set(int r, int c, PieceColor v) {
        _board[r * 19 + c] = v;
    }

    // 在位置坐标处落子
    protected void set(int k, PieceColor v) {
        _board[k] = v;
    }

    // 获得当前执子方
    public PieceColor getWhoseMove() {
        return _whoseMove;
    }

    // 确认落子，不需要撤销
    public void doMakeMove(Move move) {
        makeMove(move);
        roadTable.checkEffective(move.row0() - 'A', move.col0() - 'A');
        roadTable.checkEffective(move.row1() - 'A', move.col1() - 'A');
        //roadTable.showEffectiveRoads();
    }

    // 执行落子移动
    public void makeMove(Move move) {
        this.moveList.add(move);
        // System.out.println("index1: "  +move.index1() + ", index2: " + move.index2());
        makeStep(new Point(move.index1()));
        makeStep(new Point(move.index2()));
        _whoseMove = _whoseMove.opposite();
    }

    // 撤销落子
    public void unMakeMove(Move move) {
        this._whoseMove = this._whoseMove.opposite();
        Move mov = (Move)this.moveList.remove(this.moveList.size() - 1);
        unMakeStep(new Point(move.index2()));
        unMakeStep(new Point(move.index1()));
    }

    // 单步棋落子
    public void makeStep(Point point) {
        checkCenter(point, 1);
        set(point.getRow(), point.getCol(), _whoseMove);
        // int state = (_board[point.getIndex()] == EMPTY) ? 0 : (_board[point.getIndex()] == BLACK) ? 1 : 2;
        // hash ^= zobrist[point.getIndex()][state];
        roadTable.makeChess(point.getRow(), point.getCol(), _whoseMove);
    }

    // 撤销单步棋
    public void unMakeStep(Point point) {
        checkCenter(point, -1);
        // int state = (_board[point.getIndex()] == EMPTY) ? 0 : (_board[point.getIndex()] == BLACK) ? 1 : 2;
        // hash ^= zobrist[point.getIndex()][state];
        set(point.getRow(), point.getCol(), EMPTY);
        roadTable.unMakeChess(point.getRow(), point.getCol(), _whoseMove);
    }

    // 单步棋落子
    public void makeStep(Point point, PieceColor player) {
        set(point.getRow(), point.getCol(), player);
        roadTable.makeChess(point.getRow(), point.getCol(), player);
    }

    // 撤销单步棋
    public void unMakeStep(Point point, PieceColor player) {
        set(point.getRow(), point.getCol(), EMPTY);
        roadTable.unMakeChess(point.getRow(), point.getCol(), player);
    }

    // 更新中心区域
    public void checkCenter(Point point, int op) {
        for (int r = -2; r <= 2; r++) {
            for (int c = -2; c <= 2; c++) {
                if (validSquare(r + point.getRow(), c + point.getCol())) {
                    _center[index(r + point.getRow(), c + point.getCol())] += op;
                }
            }
        }
    }

    // 打印棋盘
    public void show() {
        for (int r = 0; r < 19; r++) {
            for (int c = 0; c < 19; c++) {
                if (_board[r * 19 + c] == WHITE)
                    System.out.print("W ");
                else if (_board[r * 19 + c] == BLACK)
                    System.out.print("B ");
                else
                    System.out.print("o ");
            }
            System.out.println();
        }
    }

    // 判读那棋子是否越界
    public static boolean validSquare(int r, int c) {
        return r >= 0 && r <= 18 && c >= 0 && c <= 18;
    }

    public static int index(int r, int c) {
        return r * 19 + c;
    }

    // 获取当前最优走步
    public ArrayList<Move> getTopMoves(int num1, int num2) {
        ArrayList<Move> moves = new ArrayList<>();
        // 获得第一步最优点
        ArrayList<Point> step1 = getTopPoint(num1);
        for (Point p1 : step1) {
            makeStep(p1);
            // 获得第二步最优点
            ArrayList<Point> step2 = getTopPoint(num2);
            for (Point p2 : step2) {
                moves.add(new Move(p1.getIndex(), p2.getIndex()));
            }
            unMakeStep(p1);
        }
        return moves;
    }

    // 获取当前评估函数评分最高的num个点
    public ArrayList<Point> getTopPoint(int num) {
        ArrayList<Point> topPoints = new ArrayList<>(5);
        HashMap<Point, Integer> scores = new HashMap<>();
        ArrayList<Point> points = getValidPoint();
        for (Point p: points) {
            makeStep(p);
            int score = roadTable.evaluation(this);
            scores.put(p, score);
            unMakeStep(p);
        }
        List<Map.Entry<Point, Integer>> scoreList = new ArrayList<>(scores.entrySet());
        scoreList.sort((o1, o2) -> (o2.getValue() - o1.getValue()));
        for (int i = 0; i < num; i++) {
            topPoints.add(scoreList.get(i).getKey());
        }
        return topPoints;
    }

    // 可落子的棋盘空位格
    public ArrayList<Point> getValidPoint() {
        // int maxRow = Math.min(18, this.maxRow + 2);
        // int maxCol = Math.min(18, this.maxCol + 2);
        // int minRow = Math.max(this.minRow - 2, 0);
        // int minCol = Math.max(this.minCol - 2, 0);
        ArrayList<Point> validPoints = new ArrayList<>();
        for (int r = 0; r < 19; r++) {
            for (int c = 0; c < 19; c++) {
                if (_center[r * 19 + c] > 0 && _board[r * 19 + c] == EMPTY) {
                    validPoints.add(new Point(r, c));
                }
            }
        }
        return validPoints;
    }

    // 判断游戏是否结束
    public boolean gameOver() {
        if (this.moveList.isEmpty()) {
            return false;
        } else {
            return roadTable.roadOfNum[6][0].size() > 0 || roadTable.roadOfNum[0][6].size() > 0;
        }
    }

    // 获取路中空余位置的点
    public List<Point> findEmptyPoints(Road road) {
        int row = road.getStartPoint().getRow();
        int col = road.getStartPoint().getCol();
        int dir = road.getDir();
        List<Point> points = new ArrayList<>();
        int r, c;
        for (int i = 0; i < 6; i++) {
            r = row + i * FORWARD[dir][0];
            c = col + i * FORWARD[dir][1];
            if (_board[r * 19 + c] == EMPTY)
                points.add(new Point(r, c));
        }
        return points;
    }

    // 获取路中空余位置的点
    public Point findConnectPoints(Road road) {
        int lastr = road.getStartPoint().getRow();
        int lastc = road.getStartPoint().getCol();
        int dir = road.getDir();
        Point p = null;
        int r, c;
        for (int i = 1; i < 6; i++) {
            r = lastr + FORWARD[dir][0];
            c = lastc + FORWARD[dir][1];
            if (_board[Point.index(r, c)] != _board[Point.index(lastr, lastc)]) {
                p = _board[Point.index(r, c)] == EMPTY ? new Point(r, c) : new Point(lastr, lastc);
                break;
            }
            lastr = r;
            lastc = c;
        }
        return p;
    }

    // 获取连2最多的点
    public Point getMaxConnectTwo() {
        int[][] scoreTwo = {{120, 0}, {250, 1750}};
        int side = (_whoseMove == BLACK) ? 0 : 1;
        HashMap<Point, Integer> scores = new HashMap<>();
        ArrayList<Point> points = getValidPoint();
        for (Point p: points) {
            makeStep(p);
            int score = roadTable.getRoadsByNum(2, _whoseMove).size() * scoreTwo[side][0]
                    - roadTable.getRoadsByNum(2, _whoseMove.opposite()).size() * scoreTwo[1 - side][0]
                    - roadTable.getRoadsByNum(3, _whoseMove.opposite()).size() * scoreTwo[1 - side][1];
            scores.put(p, score);
            unMakeStep(p);
        }
        List<Map.Entry<Point, Integer>> scoreList = new ArrayList<>(scores.entrySet());
        scoreList.sort((o1, o2) -> (o2.getValue() - o1.getValue()));
        return scoreList.get(0).getKey();
    }

    // 随机落子
    public Point getRandPoint() {
        while (true) {
            int index = rand.nextInt(SIDE * SIDE);
            if (_board[index] == EMPTY) {
                return new Point(index);
            }
        }
    }

    // 根据棋盘评估函数将move排序
    ArrayList<Move> sortMoveHeuristic(ArrayList<Move> moves) {
        HashMap<Move, Integer> scores = new HashMap<>();
        for (Move move: moves) {
            makeStep(new Point(move.index1()));
            makeStep(new Point(move.index2()));
            int score = roadTable.evaluation(this);
            scores.put(move, score);
            unMakeStep(new Point(move.index2()));
            unMakeStep(new Point(move.index1()));
        }
        List<Map.Entry<Move, Integer>> scoreList = new ArrayList<>(scores.entrySet());
        scoreList.sort((o1, o2) -> (o2.getValue() - o1.getValue()));
        ArrayList<Move> sortMoves = new ArrayList<>();
        for (Map.Entry<Move, Integer> ms: scoreList) {
            sortMoves.add((Move) ms.getKey());
        }
        return sortMoves;
    }

    // 根据棋盘评估函数将move排序
    ArrayList<Move> sortMoveHeuristic(ArrayList<Move> moves, int num) {
        HashMap<Move, Integer> scores = new HashMap<>();
        for (Move move: moves) {
            makeStep(new Point(move.index1()));
            makeStep(new Point(move.index2()));
            int score = roadTable.evaluation(this);
            scores.put(move, score);
            unMakeStep(new Point(move.index2()));
            unMakeStep(new Point(move.index1()));
        }
        List<Map.Entry<Move, Integer>> scoreList = new ArrayList<>(scores.entrySet());
        scoreList.sort((o1, o2) -> (o2.getValue() - o1.getValue()));
        ArrayList<Move> sortMoves = new ArrayList<>();
        for (int i = 0; i < num && i < scoreList.size(); i++) {
            sortMoves.add((Move) scoreList.get(i).getKey());
        }
        return sortMoves;
    }

    // 根据棋盘评估函数将move排序
    ArrayList<Move> sortMoveAttack(ArrayList<Move> moves) {
        HashMap<Move, Integer> scores = new HashMap<>();
        for (Move move: moves) {
            makeStep(new Point(move.index1()));
            makeStep(new Point(move.index2()));
            int score = roadTable.attackEva(this);
            scores.put(move, score);
            unMakeStep(new Point(move.index2()));
            unMakeStep(new Point(move.index1()));
        }
        List<Map.Entry<Move, Integer>> scoreList = new ArrayList<>(scores.entrySet());
        scoreList.sort((o1, o2) -> (o2.getValue() - o1.getValue()));
        ArrayList<Move> sortMoves = new ArrayList<>();
        for (Map.Entry<Move, Integer> ms: scoreList) {
            sortMoves.add((Move) ms.getKey());
        }
        return sortMoves;
    }

    // 根据棋盘评估函数将step-point排序
    ArrayList<Point> sortPointHeuristic(HashMap<Point, Integer> scores) {
        List<Map.Entry<Point, Integer>> scoreList = new ArrayList<>(scores.entrySet());
        scoreList.sort((o1, o2) -> (o2.getValue() - o1.getValue()));
        ArrayList<Point> sortPoints = new ArrayList<>();
        for (Map.Entry<Point, Integer> ps: scoreList) {
            sortPoints.add((Point) ps.getKey());
        }
        return sortPoints;
    }

    // 当前落子方
    private PieceColor _whoseMove;
    // 游戏棋盘
    private final PieceColor[] _board;
    // 棋盘中心
    private final int[] _center = new int[361];
    // 路表
    public RoadTable roadTable = new RoadTable();
    // 产生随机数
    public Random rand = new Random();
    // zobrist哈希随机值
    // public int[][] zobrist = new int[361][3];
    // 哈希值
    // public int hash = 0;

    public static final int[][] FORWARD = new int[][]{{0, 1}, {1, 0}, {1, 1}, {1, -1}};
    private final ArrayList<Move> moveList = new ArrayList();

    // private int maxRow = 9;
    // private int maxCol = 9;
    // private int minRow = 9;
    // private int minCol = 9;

    // maxRow = Math.min(19, Math.max(maxRow, Math.max(move.row0() - 'A', move.row1() - 'A')));
    // maxCol = Math.min(19, Math.max(maxCol, Math.max(move.col0() - 'A', move.col1() - 'A')));
    // minRow = Math.max(0, Math.min(minRow, Math.min(move.row0() - 'A', move.row1() - 'A')));
    // minCol = Math.max(0, Math.min(minCol, Math.min(move.col0() - 'A', move.col1() - 'A')));
}
