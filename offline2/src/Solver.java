import java.sql.SQLOutput;
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

    private LatinSquare backtrackWithForwardCheck(PriorityQueue<Variable> pq) {
        if (pq.isEmpty()) return ls;
        Variable v = pq.poll();
        explored++;
//        if(explored % 1000000 ==0) {
////            System.out.println("Explored: " + explored);
////            System.out.println("Backtracks: " + backtracks);
//            System.out.println(pq.size());
//        }
        ArrayList<Integer> dom=ls.leastConstrainingValue(v);
//        if(dom.size()!=v.domain.size())
//            ls.leastConstrainingValue(v);
        for (Integer i : v.domain) {
            if (!ls.isSafe(v.r, v.c, i)) {
                backtracks++;
                continue;
            }
            v.value = i;
            if (ls.isConsistent(v, i)) {
                ls.updateConstraints(v, i);
                LatinSquare res = backtrackWithForwardCheck(pq);
                if (res != null) {
                    return res;
                }
                ls.restoreConstraints(v, i);
            }
            v.value = 0;
        }
        pq.add(v);
        return null;
    }

//    private LatinSquare backtrackV2(ArrayList<Variable> ara){
//        if(ara.isEmpty()) return ls;
//        Variable v = ara.stream().min(Comparator.comparingInt(a -> a.domain.size())).get();
//        ara.remove(v);
//        explored++;
//        boolean noSafeFound=true;
//        for (Integer i : v.domain) {
//            if(!ls.isSafe(v.r, v.c, i) || !ls.isConsistent(v, i))
//        }
//    }

    public static void main(String[] args) {
        String[] files = {
//                "test_cases/d-10-01.txt",
//                "test_cases/d-10-06.txt",
//                "test_cases/d-10-07.txt",
//                "test_cases/d-10-08.txt",
//                "test_cases/d-10-09.txt",
//                "test_cases/d-15-01.txt",
        };
//        ArrayList<Comparator<Variable>> comparators = ComparatorFactory.getCmpList();
//        System.out.println("With Forward Checking");
//        for (String file : files) {
//            int i = 1;
//            System.out.println("Running test: " + file);
//            for (Comparator<Variable> c : comparators) {
//                System.out.println("--Using comparator: " + i + "---");
//                runTest(c, file, true);
//                i++;
//            }
//        }
//        System.out.println("Without Forward Checking");
//        for (String file : files) {
//            int i = 1;
//            System.out.println("Running test: " + file);
//            for (Comparator<Variable> c : comparators) {
//                System.out.println("--Using comparator: " + i + "---");
//                runTest(c, file, false);
//                i++;
//            }
//        }
        LatinSquare board = Util.readTestCase("test_cases/d-10-01.txt");
        long t1 = System.nanoTime();
        board.initDomains();
        LatinSquare res = new Solver(board, ComparatorFactory.preferDomComp()).solve(false);
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
