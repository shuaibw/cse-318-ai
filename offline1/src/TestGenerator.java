import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class TestGenerator {
    private int k;
    private int[][] board;

    public TestGenerator(int k) {
        this.k = k;
    }

    // Generate a random board
    public int[][] generateRandomBoard() {
        board = new int[k][k];
        int[] arr = new int[k * k];
        for (int i = 0; i < k * k; i++) {
            arr[i] = i;
        }
        shuffle(arr);
        while (inversions(arr) % 2 != 0) {
            shuffle(arr);
        }
        int index = 0;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                board[i][j] = arr[index++];
            }
        }
        return board;
    }

    private int inversions(int[] arr) {
        int inversions = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] != 0 && arr[j] != 0 && arr[i] > arr[j]) {
                    inversions++;
                }
            }
        }
        return inversions;
    }

    private void shuffle(int[] array) {
        int index;
        Random random = new Random(System.currentTimeMillis());
        for (int i = array.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            if (index != i) {
                array[index] ^= array[i];
                array[i] ^= array[index];
                array[index] ^= array[i];
            }
        }
    }

    public static void main(String[] args) throws IOException {
        int k = 3;
        TestGenerator tg = new TestGenerator(k);
        PrintStream ps = new PrintStream("src/k3tests.txt");
        System.setOut(ps);
        for (int i = 0; i < 20; i++) {
            System.out.println("Test: " + (i + 1));
            int[][] board = tg.generateRandomBoard();
            NPuzzleState.resetHashSet();
            NPuzzleState state = new NPuzzleState(k, board);
            state.printBoardClassic();
            boolean solvable = state.solvable();
            System.out.println("Solvable: " + solvable);
            if (!solvable) return;
            NPuzzleSolver solver = new NPuzzleSolver(k, board);
            ArrayList<NPuzzleState> path = solver.solve("hamming");
            System.out.println("Total moves: " + (path.size() - 1));
            System.out.println("Moves: " + solver.getMoves());
//            for (NPuzzleState s : path) {
//                System.out.println(s.getMove());
//                s.printBoard();
//            }
            System.out.println("-----------------------------------");
        }
    }
}
