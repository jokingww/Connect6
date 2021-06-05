package g04.Group04;

public class Node {
    // 搜索深度
    private int depth;
    // 评估函数值
    private int evaluation;

    public Node(int depth, int evaluation) {
        this.depth = depth;
        this.evaluation = evaluation;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(int evaluation) {
        this.evaluation = evaluation;
    }
}
