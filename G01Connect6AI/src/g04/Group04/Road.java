package g04.Group04;

import core.board.PieceColor;

import java.util.Vector;

import static core.board.PieceColor.*;

public class Road {
    // 路中起点棋子坐标
    private Point startPoint;
    /** 路的方向
     * 0: 右, 1: 下, 2: 右下, 3: 右上
     */
    private int dir;
    /** 路的颜色
     * 0: 无颜色, 1: 黑色, 2: 白色, 3: 两种颜色
     */
    private int type;

    // 路的有效性
    private boolean effective = false;
    // 黑棋数
    private int blackChessNum = 0;
    // 白棋数
    private int whiteChessNum = 0;

    // 有效路中棋子数
    // private int num;
    // 路中所有棋子
    // private Vector<PieceColor> chesses = new Vector<>(6);

    public Road(Vector<PieceColor> chesses) {
        this.type = 0;
        for (PieceColor chess: chesses) {
            if (chess == BLACK) {
                blackChessNum++;
            } else if (chess == WHITE) {
                whiteChessNum++;
            }
        }
        if (blackChessNum > 0 && whiteChessNum == 0){
            this.type = 1;
        } else if(whiteChessNum > 0 && blackChessNum == 0){
            this.type = 2;
        }  else if(whiteChessNum > 0 && blackChessNum > 0){
            this.type = 3;
        } else {
            this.type = 0;
        }
    }

    // 根据起点位置和方向确定唯一的路
    public Road(int row, int col, int dir) {
        this.type = 0;
        this.startPoint = new Point(row, col);
        this.dir = dir;
        this.effective = true;
    }

    // 在路中落子
    public void makeChess(PieceColor player) {
        if (player == BLACK) {
            blackChessNum++;
        } else if (player == WHITE) {
            whiteChessNum++;
        }
    }

    // 撤销在路中落子
    public void unMakeChess(PieceColor player) {
        if (player == BLACK) {
            blackChessNum--;
        } else if (player == WHITE) {
            whiteChessNum--;
        }
    }

    @Override
    public int hashCode() {
        return startPoint.getIndex() + dir * 361;
    }

    @Override
    public String toString() {
        String str;
        str =  getStartPoint() + ", d: " + getDir();
        if (effective) {
            if (blackChessNum > 0)
                str += ", BLACK: "  + blackChessNum;
            if (whiteChessNum > 0)
                str += ", WHITE: "  + whiteChessNum;
        }
        return str;
    }

    public boolean isEffective() {
        return effective;
    }

    public void setEffective(boolean effective) {
        this.effective = effective;
    }

    public int blackChessNum() {
        return blackChessNum;
    }

    public int whiteChessNum() {
        return whiteChessNum;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public int getDir() {
        return dir;
    }

    // public Vector<PieceColor> getChesses() {
    //    return chesses;
    // }
}
