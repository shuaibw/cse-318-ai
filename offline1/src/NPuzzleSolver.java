import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;

public class NPuzzleSolver {
    private final int k;
    private final int[][] board;
    private final int[][] goalBoard;
    private ArrayList<String> moves;

    public NPuzzleSolver(int k, int[][] n) {
        this.k = k;
        board = new int[k][k];
        for (int i = 0; i < k; i++) {
            System.arraycopy(n[i], 0, board[i], 0, k);
        }
        goalBoard = new int[k][k];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                if (i == k - 1 && j == k - 1) goalBoard[i][j] = 0;
                else goalBoard[i][j] = i * k + j + 1;
            }
        }
    }

    public ArrayList<NPuzzleState> solve(String heuristic) {
        NPuzzleState start = new NPuzzleState(k, board);
        if (!start.solvable()) {
            System.out.println("This puzzle is not solvable");
            return null;
        }
        NPuzzleState goal = new NPuzzleState(k, goalBoard);
        Comparator<NPuzzleState> cmp;
        if (heuristic.equalsIgnoreCase("manhattan"))
            cmp = Comparator.comparingInt(a -> a.getDistance() + a.getManhattanDistance());
        else cmp = Comparator.comparingInt(a -> a.getDistance() + a.getHammingDistance());
        return solve(start, goal, cmp);
    }

    private ArrayList<NPuzzleState> solve(NPuzzleState start, NPuzzleState goal, Comparator<NPuzzleState> cmp) {
        // solve using A* search, use Manhattan distance as heuristic
        // return the path from start to goal
        // if no solution, return null
        PriorityQueue<NPuzzleState> queue = new PriorityQueue<>(cmp);
        queue.add(start);
        while (!queue.isEmpty()) {
            NPuzzleState cur = queue.poll();
            if (cur.equals(goal)) {
                return calculatePath(cur);
            }
            for (NPuzzleState neighbor : cur.getNeighbors()) {
                if (!neighbor.visited) {
                    neighbor.visited = true;
                    neighbor.setPrev(cur);
                    neighbor.setDistance(cur.getDistance() + 1);
                    queue.add(neighbor);
                }
            }
        }
        return null;
    }

    private ArrayList<NPuzzleState> calculatePath(NPuzzleState goal) {
        ArrayList<NPuzzleState> path = new ArrayList<>();
        moves = new ArrayList<>();
        while (goal != null) {
            moves.add(goal.getMove());
            path.add(goal);
            goal = goal.getPrev();
        }
        Collections.reverse(path);
        Collections.reverse(moves);
        return path;
    }

    public ArrayList<String> getMoves() {
        if (moves == null) {
            System.out.println("No solution found");
            return null;
        }
        return moves;
    }

    public static void printSolve(int k, int[][] n, String heuristic) {
        NPuzzleSolver solver = new NPuzzleSolver(k, n);
        ArrayList<NPuzzleState> path = solver.solve("manhattan");
        if (path == null) {
            System.out.println("No solution");
            return;
        }
        System.out.println("Using " + heuristic + " distance as heuristic");
        System.out.println("Total moves: " + (path.size() - 1));
        System.out.println("Moves: " + solver.getMoves());
        for (NPuzzleState state : path) {
            System.out.println(state.getMove());
            state.printBoard();
        }
    }

    public static void main(String[] args) {
        int k = 4;
        int[][] n = {
                {2, 3, 7, 4},
                {1, 6, 11, 8},
                {5, 10, 0, 15},
                {9, 13, 12, 14}
        };
        printSolve(k, n, "Manhattan");
        System.out.println();
        printSolve(k, n, "Hamming");
    }
}
