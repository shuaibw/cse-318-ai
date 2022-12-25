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

    public LatinSquare solve(boolean useFc) {
        ArrayList<Variable> vars = ls.getVars();
        PriorityQueue<Variable> pq = new PriorityQueue<>(cmp);
        pq.addAll(vars);
        LatinSquare sol = useFc ? backtrackWithForwardCheck(pq) : backtrack(pq);
        System.out.println("Explored: " + explored);
        System.out.println("Backtracks: " + backtracks);
        return sol;
    }

    private LatinSquare backtrack(PriorityQueue<Variable> pq) {
        if (pq.isEmpty()) return ls;
        Variable v = pq.poll();
        explored++;
//        if(explored % 1000000 ==0) {
////            System.out.println("Explored: " + explored);
////            System.out.println("Backtracks: " + backtracks);
//            System.out.println(pq.size());
//        }
        boolean noSafeFound = true;
        for (Integer i : v.domain) {
            if (!ls.isSafe(v.r, v.c, i)) continue;
            noSafeFound = false;
            v.value = i;
            LatinSquare res = backtrack(pq);
            if (res != null) {
                return res;
            }
            v.value = 0;
        }
        if (noSafeFound) backtracks++;
        pq.add(v);
        return null;
    }

    private LatinSquare backtrackWithForwardCheck(PriorityQueue<Variable> pq) {
        if (pq.isEmpty()) return ls;
        Variable v = pq.poll();
        explored++;
//        if(explored % 1000000 ==0) {
////            System.out.println("Explored: " + explored);
////            System.out.println("Backtracks: " + backtracks);
//            System.out.println(pq.size());
//        }
        boolean noSafeFound = true;
        for (Integer i : v.domain) {
            if (!ls.isSafe(v.r, v.c, i) || !ls.isConsistent(v, i)) continue;
            noSafeFound = false;
            int oldVal = v.value;
            v.value = i;
            ls.updateConstraints(v, i);
            LatinSquare res = backtrack(pq);
            if (res != null) {
                return res;
            }
            ls.restoreConstraints(v, oldVal);
            v.value = 0;
        }
        if (noSafeFound) backtracks++;
        pq.add(v);
        return null;
    }

    public static void main(String[] args) {
        String[] files = {
                "../test_cases/d-10-01.txt",
                "../test_cases/d-10-06.txt",
                "../test_cases/d-10-07.txt",
                "../test_cases/d-10-08.txt",
                "../test_cases/d-10-09.txt",
                "../test_cases/d-15-01.txt",
        };
        ArrayList<Comparator<Variable>> comparators = ComparatorFactory.getCmpList();
        System.out.println("With Forward Checking");
        for (String file : files) {
            int i = 1;
            System.out.println("Running test: " + file);
            for (Comparator<Variable> c : comparators) {
                System.out.println("--Using comparator: " + i + "---");
                runTest(c, file, true);
                i++;
            }
        }
        System.out.println("Without Forward Checking");
        for (String file : files) {
            int i = 1;
            System.out.println("Running test: " + file);
            for (Comparator<Variable> c : comparators) {
                System.out.println("--Using comparator: " + i + "---");
                runTest(c, file, false);
                i++;
            }
        }
    }

    public static void runTest(Comparator<Variable> cmp, String filePath, boolean useFc) {
        LatinSquare board = Util.readTestCase(filePath);
        board.initDomains();
        LatinSquare res = new Solver(board, cmp).solve(useFc);
        System.out.println(res);
    }
}
