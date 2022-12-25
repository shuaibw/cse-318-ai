import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;

public class Solver {
    LatinSquare ls;
    long explored = 0;
    long backtracks = 0;
    Comparator<Variable> cmp;

    public Solver(LatinSquare ls, Comparator<Variable> cmp) {
        this.ls = ls;
        this.cmp = cmp;
    }

    public LatinSquare solve() {
        ArrayList<Variable> vars = ls.getVars();
        PriorityQueue<Variable> pq = new PriorityQueue<>(cmp);
        pq.addAll(vars);
        LatinSquare sol = backtrack(pq);
        System.out.println("Explored: " + explored);
        System.out.println("Backtracks: " + backtracks);
        return sol;
    }

    private LatinSquare backtrack(PriorityQueue<Variable> pq) {
        if (pq.isEmpty()) return ls;
        Variable v = pq.poll();
        explored++;
        if(explored % 1000000 ==0) {
//            System.out.println("Explored: " + explored);
//            System.out.println("Backtracks: " + backtracks);
            System.out.println(pq.size());
        }
        boolean noSafeFound = true;
        for (Integer i : v.domain) {
            if (!ls.isSafe(v.r, v.c, i)) continue;
            noSafeFound=false;
            v.value = i;
            LatinSquare res = backtrack(pq);
            if (res != null) {
                return res;
            }
            v.value = 0;
        }
        if(noSafeFound) backtracks++;
        pq.add(v);
        return null;
    }

    public static void main(String[] args) {
        LatinSquare board = Util.readTestCase("test_cases/d-10-09.txt");
        board.initDomains();
        LatinSquare res = new Solver(board, ComparatorFactory.preferDomComp()).solve();
        System.out.println(res);
    }
}
