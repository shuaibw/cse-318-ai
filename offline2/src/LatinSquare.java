import java.util.*;

public class LatinSquare {
    public final int n;
    public final Variable[][] board;
    public ArrayList<Variable> vars;

    private class LRVCell {
        int val, affected;

        public LRVCell(int val, int affected) {
            this.val = val;
            this.affected = affected;
        }
    }

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
        v.degree = degree;
    }

    public boolean isSafe(int i, int j, int num) {
        // check row and col
        for (int k = 0; k < n; k++) {
            if (board[i][k].value == num && k != j) return false;
            if (board[k][j].value == num && k != i) return false;
        }
        return true;
    }

    public boolean isConsistent(Variable v, int num) {
        // check along row, col and see if assigning num to v makes any cell's domain empty
        for (int i = 0; i < n; i++) {
            if (i != v.r) {
                Variable r = board[i][v.c];
                if (r.value == 0 && r.domain.size() == 1 && r.domain.contains(num)) return false;
            }
            if (i != v.c) {
                Variable c = board[v.r][i];
                if (c.value == 0 && c.domain.size() == 1 && c.domain.contains(num)) return false;
            }
        }
        return true;
    }

    public void updateConstraints(Variable v, int newVal, HashSet<Variable> set, PriorityQueue<Variable> pq) {
        for (int i = 0; i < n; i++) {
            if (i != v.r) {
                Variable r = board[i][v.c];
                updateVar(newVal, set, pq, r);

            }
            if (i != v.c) {
                Variable c = board[v.r][i];
                updateVar(newVal, set, pq, c);
            }
        }
    }

    private void updateVar(int newVal, HashSet<Variable> set, PriorityQueue<Variable> pq, Variable r) {
        boolean containsVal = r.domain.contains(newVal);
        boolean isZero = r.value == 0;
        if (isZero) r.degree--;
        if (containsVal) {
            r.domain.remove(newVal);
            set.add(r);
        }
        if (isZero || containsVal) {
            if (pq.remove(r)) pq.add(r);
        }
    }

    public void restoreConstraints(Variable v, int val, HashSet<Variable> set, PriorityQueue<Variable> pq) {
        for (int i = 0; i < n; i++) {
            if (i != v.r) {
                Variable r = board[i][v.c];
                restoreVar(r, set, val, pq);
            }
            if (i != v.c) {
                Variable c = board[v.r][i];
                restoreVar(c, set, val, pq);
            }
        }
    }

    private void restoreVar(Variable r, HashSet<Variable> set, int val, PriorityQueue<Variable> pq) {
        boolean isZero = r.value == 0;
        boolean containsVal = set.contains(r);
        if (isZero) r.degree++;
        if (containsVal) {
            r.domain.add(val);
            set.remove(r);
        }
        if (isZero || containsVal) {
            if (pq.remove(r)) pq.add(r);

        }
    }

    public ArrayList<Integer> leastConstrainingValue(Variable v) {
        ArrayList<Integer> orderedDom = new ArrayList<>();
        LRVCell[] map = new LRVCell[n + 1];
        for (Integer i : v.domain) {
            map[i] = new LRVCell(i, countCrossOffs(v.r, v.c, i));
        }
        // keeps the null cells at the end, non-null cells at the beginning
        Arrays.sort(map, (a, b) -> {
            if (a == null && b == null) return 0;
            if (a == null) return 1;
            if (b == null) return -1;
            return Integer.compare(a.affected, b.affected);
        });

        int start = 0;
        while (map[start] != null) {
            orderedDom.add(map[start].val);
            start++;
        }
        return orderedDom;
    }

    private int countCrossOffs(int row, int col, int val) {
        int cuts = 0;
        for (int i = 0; i < n; i++) {
            if (i != row) {
                Variable c = board[i][col];
                if (c.value == 0 && c.domain.contains(val)) cuts++;
            }
            if (i != col) {
                Variable r = board[row][i];
                if (r.value == 0 && r.domain.contains(val)) cuts++;
            }
        }
        return cuts;
    }

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
