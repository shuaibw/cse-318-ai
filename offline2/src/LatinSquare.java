import java.util.ArrayList;

public class LatinSquare {
    public final int n;
    public final Variable[][] board;
    public ArrayList<Variable> vars;

    public LatinSquare(int n) {
        this.n = n;
        board = new Variable[n][n];
    }

    public void initDomains() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j].value == 0) {
                    for (int k = 1; k <= n; k++) {
                        if (!isSafe(i, j, k)) continue;
                        board[i][j].domain.add(k);
                    }
                }
            }
        }
    }

    public ArrayList<Variable> getVars() {
        if (vars != null) return vars;
        vars = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (board[i][j].value != 0) continue;
                setVarDegree(board[i][j]);
                vars.add(board[i][j]);
            }
        }
        return vars;
    }

    private void setVarDegree(Variable v) {
        int degree = 0;
        for (int i = 0; i < n; i++) {
            if (board[v.r][i].value == 0 && i != v.c) degree++;
            if (board[i][v.c].value == 0 && i != v.r) degree++;
        }
//        Variable top = v.r - 1 >= 0 ? board[v.r - 1][v.c] : null;
//        Variable bottom = v.r + 1 < n ? board[v.r + 1][v.c] : null;
//        Variable left = v.c - 1 >= 0 ? board[v.r][v.c - 1] : null;
//        Variable right = v.c + 1 < n ? board[v.r][v.c + 1] : null;
//        if (top != null && top.value == 0) degree++;
//        if (bottom != null && bottom.value == 0) degree++;
//        if (left != null && left.value == 0) degree++;
//        if (right != null && right.value == 0) degree++;
        v.degree = degree;
    }

    public boolean isSafe(int i, int j, int num) {
        // check row and col
        for (int k = 0; k < n; k++) {
            if (board[i][k].value == num || board[k][j].value == num) return false;
        }
        return true;
    }

    public boolean isConsistent(Variable v, int num) {
        // check along row, col and see if assigning num to v makes any cell's domain empty
        for (int i = 0; i < n; i++) {
            if (i == v.r || i == v.c) continue;
            Variable r = board[i][v.c];
            Variable c = board[v.r][i];
            if (r.value == 0 && r.domain.size() == 1 && r.domain.contains(num)) return false;
            if (c.value == 0 && c.domain.size() == 1 && c.domain.contains(num)) return false;
        }
        return true;
    }

    public void updateConstraints(Variable v, int newVal) {
        for (int i = 0; i < n; i++) {
            if (i == v.r || i == v.c) continue;
            Variable r = board[i][v.c];
            Variable c = board[v.r][i];
            boolean rRemoved = r.value == 0 && r.domain.remove(newVal);
            boolean cRemoved = c.value == 0 && c.domain.remove(newVal);
            if (rRemoved) r.degree--;
            if (cRemoved) c.degree--;
        }
    }

    public void restoreConstraints(Variable v, int oldVal) {
        for (int i = 0; i < n; i++) {
            if (i == v.r || i == v.c) continue;
            Variable r = board[i][v.c];
            Variable c = board[v.r][i];
            if (r.value == 0) {
                r.domain.add(oldVal);
                r.degree++;
            }
            if (c.value == 0) {
                c.domain.add(oldVal);
                c.degree++;
            }
        }
    }
//    public int leastConstrainingValue(Variable v){
//        ArrayList<Integer> domain=new ArrayList<>();
//        for (Integer i : v.domain) {
//
//        }
//    }

    @Override
    public String toString() {
        // Generate a string representation of the board
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sb.append(board[i][j].value);
                sb.append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public LatinSquare createCopy() {
        LatinSquare clone = new LatinSquare(n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Variable v = new Variable(i, j, board[i][j].value);
                v.degree = board[i][j].degree;
                clone.board[i][j] = v;
            }
        }
        return clone;
    }
}
