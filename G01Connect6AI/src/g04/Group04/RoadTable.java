package g04.Group04;

import core.board.PieceColor;
import static core.board.PieceColor.*;

import java.util.HashMap;

public class RoadTable {
    // 棋路方向
    public int[][] FORWARD = new int[][]{{0, 1}, {1, 0}, {1, 1}, {1, -1}};
    // 评估系数
    // public final int[] roadScore = {0, 17, 78, 141, 788, 1030, 10000};
    public int[][] roadScore =  {
            { 0, 9, 120, 2070, 7890, 0, 1000000 },
            { 0, 3, 250, 1750, 3887, 4900, 1000000 }
    };
    // { 0, 9, 480, 2070, 10000, 10020, 1000000 },
    // { 0, 3, 520, 2670, 4750, 4900, 1000000 }
    // { 0, 9, 520, 2070, 7890, 10020, 1000000 },
    // { 0, 3, 480, 2670, 3887, 4900, 1000000 }

    // 所有棋路
    public Road[][][] roads = new Road[19][19][4];
    // 根据黑白棋数对路分类
    public HashMap<Integer, Road>[][] roadOfNum = new HashMap[7][7];

    // 记录黑白路的数目
    // public static int[] blackRoadsNum = new int[]{0, 0, 0, 0, 0, 0, 0};
    // public static int[] whiteRoadsNum = new int[]{0, 0, 0, 0, 0, 0, 0};

    public void setScore(PieceColor player) {
        roadScore =  new int[][]{
                { 0, 9, 120, 2070, 7890, 10020, 1000000 },
                { 0, 3, 250, 1750, 3887, 4900, 1000000 }
        };
    }

    // 初始化路表
    public RoadTable() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                roadOfNum[i][j] = new HashMap<>();
            }
        }
        // 初始化所有路
        for (int r = 0; r < 19; r++) {
            for (int c = 0; c < 19; c++) {
                for (int d = 0; d < 4; d++) {
                    roads[r][c][d] = new Road(r, c, d);
                    if (!MyBoard.validSquare(r + 5 * FORWARD[d][0], c + 5 * FORWARD[d][1])) {
                        roads[r][c][d].setEffective(false);
                    } else {
                        roadOfNum[0][0].put(roads[r][c][d].hashCode(), roads[r][c][d]);
                    }
                }
            }
        }
        System.out.println("size: " + roadOfNum[0][0].size());
    }

    // 对给定落子修改路表
    public void makeChess(int row, int col, PieceColor player) {
        for (int d = 0; d < 4; d++) {
            int r , c;
            for (int i = 0; i < 6; i++) {
                r = row - i * FORWARD[d][0];
                c = col - i * FORWARD[d][1];
                // 计算起点位置是否合法
                if (!MyBoard.validSquare(r, c))
                    break;
                Road road = roads[r][c][d];
                if (!road.isEffective())
                    continue;
                // 路中棋子数更改，因此路在roadOfNum中也需要更改
                roadOfNum[road.blackChessNum()][road.whiteChessNum()].remove(road.hashCode());
                road.makeChess(player);
                roadOfNum[road.blackChessNum()][road.whiteChessNum()].put(road.hashCode(), road);
            }
        }
    }

    // 撤销给定落子对路表的修改
    public void unMakeChess(int row, int col, PieceColor player) {
        for (int d = 0; d < 4; d++) {
            int r , c;
            for (int i = 0; i < 6; i++) {
                r = row - i * FORWARD[d][0];
                c = col - i * FORWARD[d][1];
                if (!MyBoard.validSquare(r, c))
                    break;
                Road road = roads[r][c][d];
                if (!road.isEffective())
                    continue;
                roadOfNum[road.blackChessNum()][road.whiteChessNum()].remove(road.hashCode());
                road.unMakeChess(player);
                roadOfNum[road.blackChessNum()][road.whiteChessNum()].put(road.hashCode(), road);
            }
        }
    }

    // 检查路的有效性
    public void checkEffective(int row, int col) {
        for (int d = 0; d < 4; d++) {
            int r, c;
            for (int i = 0; i < 6; i++) {
                r = row - i * FORWARD[d][0];
                c = col - i * FORWARD[d][1];
                if (!MyBoard.validSquare(r, c))
                    break;
                Road road = roads[r][c][d];
                if (!road.isEffective())
                    continue;
                // 路中有两种颜色，视为无效
                if (road.blackChessNum() > 0 && road.whiteChessNum() > 0)
                    road.setEffective(false);
            }
        }
    }

    public HashMap<Integer, Road> getRoadsByNum(int n, PieceColor player) {
        return player == BLACK ? roadOfNum[n][0] : roadOfNum[0][n];
    }

    // 棋局评估函数
    public int evaluation(MyBoard board) {
        int score = 0;
        PieceColor player = board.getWhoseMove();
        int side = (player == BLACK) ? 0 : 1;
        // 根据各种有效路数目获得评分
        for (int i = 1; i < 7; i++) {
            score += roadOfNum[i][0].size() * roadScore[side][i] -  roadOfNum[0][i].size() * roadScore[1 - side][i];
        }
        return player == PieceColor.BLACK ? score : -score;
    }

    // 棋局评估函数
    public int attackEva(MyBoard board) {
        int score = 0;
        PieceColor player = board.getWhoseMove();
        int side = (player == BLACK) ? 0 : 1;
        // 根据各种有效路数目获得评分
        for (int i = 1; i < 7; i++) {
            score += roadOfNum[side][0].size() * roadScore[0][i];
        }
        return score;
    }

    // 打印所有的有效路数
    public void showEffectiveRoads() {
        for (int i = 1; i < 7; i++) {
            for (Road road: roadOfNum[i][0].values()) {
                System.out.println(road);
            }
            for (Road road: roadOfNum[0][i].values()) {
                System.out.println(road);
            }
        }
    }

    // 计算所有棋路
    /*
    public void getRoads(MyBoard board) {
        Vector<Road> roads = new Vector<>();
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 14; j++) {
                Vector<PieceColor> horizontal = new Vector<>(6);
                horizontal.add(board.get(i, j));
                horizontal.add(board.get(i, j + 1));
                horizontal.add(board.get(i, j + 2));
                horizontal.add(board.get(i, j + 3));
                horizontal.add(board.get(i, j + 4));
                horizontal.add(board.get(i, j + 5));
                Road hR = new Road(horizontal);
                roads.add(hR);
                Vector<PieceColor> vertical = new Vector<>(6);
                vertical.add(board.get(j, i));
                vertical.add(board.get(j + 1, i));
                vertical.add(board.get(j + 2, i));
                vertical.add(board.get(j + 3, i));
                vertical.add(board.get(j + 4, i));
                vertical.add(board.get(j + 5, i));
                Road vR = new Road(vertical);
                roads.add(vR);
            }
        }
        for (int i = 0; i < 14; i++) {
            for (int j = 0; j < 14; j++) {
                Vector<PieceColor> leftOblique = new Vector<>(6);
                leftOblique.add(board.get(i, j));
                leftOblique.add(board.get(i + 1, j + 1));
                leftOblique.add(board.get(i + 2, j + 2));
                leftOblique.add(board.get(i + 3, j + 3));
                leftOblique.add(board.get(i + 4, j + 4));
                leftOblique.add(board.get(i + 5, j + 5));
                Road lR = new Road(leftOblique);
                roads.add(lR);
                Vector<PieceColor> rightOblique = new Vector<>(6);
                rightOblique.add(board.get(i, j + 5));
                rightOblique.add(board.get(i + 1, j + 4));
                rightOblique.add(board.get(i + 2, j + 3));
                rightOblique.add(board.get(i + 3, j + 2));
                rightOblique.add(board.get(i + 4, j + 1));
                rightOblique.add(board.get(i + 5, j));
                Road rR = new Road(rightOblique);
                roads.add(rR);
            }
        }
        blackRoadsNum  = new int[]{0, 0, 0, 0, 0, 0, 0};
        whiteRoadsNum = new int[]{0, 0, 0, 0, 0, 0, 0};
        for (Road road: roads) {
            if (road.getType() == 1) {
                blackRoadsNum[road.getNum()]++;
                //System.out.println(road.getChesses());
            } else if (road.getType() == 2) {
                whiteRoadsNum[road.getNum()]++;
                //System.out.println(road.getChesses());
            }
        }
    }
    */

    /*
    public int evaluation(MyBoard board) {
        int score = 0;
        int blackRoadScore = 0;
        int whiteRoadScore = 0;
        PieceColor player = board.getWhoseMove();
        // getRoads(board);
        for (int i = 1; i < 7; i++) {
            // blackRoadScore += blackRoadsNum[i] * roadScore[i];
            // whiteRoadScore += whiteRoadsNum[i] * roadScore[i];
            blackRoadScore += blackRoads[i].size() * roadScore[i];
            whiteRoadScore += whiteRoads[i].size() * roadScore[i];
        }
        if (player == PieceColor.BLACK) {
            score = blackRoadScore - whiteRoadScore;
        }
        if (player == PieceColor.WHITE) {
            score = whiteRoadScore - blackRoadScore;
        }
        return score;
    }
    */
}
