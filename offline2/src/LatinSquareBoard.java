import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;

public class LatinSquareBoard {
    private final int n;
    private final int[][] board;

    public LatinSquareBoard(int n) {
        this.n = n;
        board = new int[n][n];
    }

    public static LatinSquareBoard readTestCase(String file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            int n = Integer.parseInt(br.readLine().split("=")[1].replaceAll(";", ""));
            LatinSquareBoard latinSquareBoard=new LatinSquareBoard(n);
            br.readLine();
            String input;
            int i = 0;
            while ((input = br.readLine()) != null) {
                // remove leading and trailing [,],|
                input = input.replaceAll("[\\[\\];\\|]", "");
                String[] tokens = input.split(",");
                if(tokens.length!=n) continue;
                for (int j = 0; j < n; j++) {
                    latinSquareBoard.board[i][j] = Integer.parseInt(tokens[j].strip());
                }
                i++;
            }
            return latinSquareBoard;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        // Generate a string representation of the board
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sb.append(board[i][j]);
                sb.append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        LatinSquareBoard board = LatinSquareBoard.readTestCase("test_cases/d-10-01.txt");
        System.out.println(board);
    }
}
