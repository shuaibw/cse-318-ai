import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

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
        TreeSet<Variable> pq = new TreeSet<>(cmp);
//        PriorityQueue<Variable> pq = new PriorityQueue<>(cmp);
        pq.addAll(vars);
        LatinSquare sol = useFc ? backtrackWithForwardCheck(pq) : backtrack(pq);
        System.out.println("Explored: " + (explored + backtracks));
        System.out.println("Backtracks: " + backtracks);
        return sol;
    }

    private LatinSquare backtrack(TreeSet<Variable> pq) {
        Variable v = pq.pollFirst();
        if (v == null) return ls;
        explored++;
        for (Integer i : ls.leastConstrainingValue(v)) {
            if (!ls.isSafe(v.r, v.c, i)) {
                backtracks++;
                continue;
            }
            v.value = i;
            LatinSquare res = backtrack(pq);
            if (res != null) {
                return res;
            }
            v.value = 0;
        }
        pq.add(v);
        return null;
    }

    private LatinSquare backtrackWithForwardCheck(TreeSet<Variable> pq) {
        Variable v = pq.pollFirst();
        if (v == null) return ls;
        explored++;
        for (Integer i : ls.leastConstrainingValue(v)) {
            v.value = i;
            v.domain.remove(i);
            if (ls.isConsistent(v, i)) {
                HashSet<Variable> updatedNodes = new HashSet<>();
                ls.updateConstraints(v, i, updatedNodes);
                LatinSquare res = backtrackWithForwardCheck(pq);
                if (res != null) {
                    return res;
                }
                ls.restoreConstraints(v, i, updatedNodes);
            }
            backtracks++;
            v.value = 0;
            v.domain.add(i);
        }
        pq.add(v);
        return null;
    }

    public static void main(String[] args) throws Exception {
//        MultiPrintStream ms=new MultiPrintStream(
//                new PrintStream(System.out, true),
//                new PrintStream(new FileOutputStream("log.txt"), true)
//        );
//        System.setOut(ms);
//        runAll();
        runSingle("test_cases/d-15-01.txt");
    }

    private static void runAll() {
        String[] files = {
                "test_cases/d-10-01.txt",
                "test_cases/d-10-06.txt",
                "test_cases/d-10-07.txt",
                "test_cases/d-10-08.txt",
                "test_cases/d-10-09.txt",
                "test_cases/d-15-01.txt",
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

    private static void runSingle(String filename) {
        LatinSquare board = Util.readTestCase(filename);
        long t1 = System.nanoTime();
        board.initDomains();
        LatinSquare res = new Solver(board, ComparatorFactory.preferDomComp()).solve(true);
        long t2 = System.nanoTime();
        System.out.println("Time in ms: " + (t2 - t1) / 1000000.0);
        System.out.println(res);
    }

    public static void runTest(Comparator<Variable> cmp, String filePath, boolean useFc) {
        LatinSquare board = Util.readTestCase(filePath);
        long t1 = System.nanoTime();
        board.initDomains();
        LatinSquare res = new Solver(board, cmp).solve(useFc);
        long t2 = System.nanoTime();
        System.out.println("Time in ms: " + (t2 - t1) / 1000000.0);
        System.out.println(res);
    }
}
