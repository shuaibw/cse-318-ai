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
        NPuzzleSolver.printSolve(k, board, "manhattan");
        System.out.println("******************");
        NPuzzleSolver.printSolve(k, board, "hamming");
    }
}
