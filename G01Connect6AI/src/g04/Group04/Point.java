package g04.Group04;

public class Point {
    // 点在棋盘中的行和列
    private int row;
    private int col;
    private int index;

    public Point(int row, int col) {
        this.row = row;
        this.col = col;
        this.index = row * 19 + col;
    }

    public Point(int index) {
        this.row = index / 19;
        this.col = index % 19;
        this.index = index;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        else if (obj instanceof Point) {
            Point p = (Point) obj;
            return p.row == this.row && p.col == this.col;
        }
        else return false;
    }

    @Override
    public int hashCode() {
        return getIndex();
    }

    @Override
    public String toString() {
        return "P row: " + row + ", col: " + col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    // 点在一维棋盘数组中的偏移量
    public int getIndex() {
        return index;
    }

    // 点在一维棋盘数组中的偏移量
    public static int index(int r, int c) {
        return r * 19 + c;
    }
}
