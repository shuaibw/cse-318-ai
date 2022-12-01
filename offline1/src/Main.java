import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int k;
        int[][] board;
        Scanner scanner = new Scanner(System.in);
        k = scanner.nextInt();
        board = new int[k][k];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                board[i][j] = scanner.nextInt();
            }
        }
        NPuzzleSolver solver = new NPuzzleSolver(k, board);
        ArrayList<NPuzzleState> path = solver.solve("manhattan");
        if(path==null){
            System.out.println("Odd inversion, no solution");
            return;
        }
        System.out.println("Manhattan heuristic");
        System.out.println("Total moves: " + (path.size() - 1));
        System.out.println("Moves: " + solver.getMoves());
        for (NPuzzleState s : path) {
            brightPrint(s.getMove());
            s.printBoard();
        }

    }
    public static void brightPrint(String s){
        System.out.println("\u001B[0;93m"+s+"\u001B[0m");
    }
}
