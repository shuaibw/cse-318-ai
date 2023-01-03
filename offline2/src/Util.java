import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

public class Util {
    public static LatinSquare readTestCase(String file) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            int n = Integer.parseInt(br.readLine().split("=")[1].replaceAll(";", ""));
            LatinSquare latinSquare = new LatinSquare(n);
            br.readLine();
            String input;
            int i = 0;
            while ((input = br.readLine()) != null) {
                // remove leading and trailing [,],|
                input = input.replaceAll("[\\[\\];\\|]", "");
                String[] tokens = input.split(",");
                if (tokens.length != n) continue;
                for (int j = 0; j < n; j++) {
                    int val = Integer.parseInt(tokens[j].strip());
                    latinSquare.board[i][j] = new Variable(i, j, val);
                }
                i++;
            }
            return latinSquare;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static LatinSquare readUserInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter n:");
        int n = scanner.nextInt();
        System.out.println("Enter board elements, separated by spaces");
        LatinSquare latinSquare = new LatinSquare(n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                latinSquare.board[i][j] = new Variable(i, j, scanner.nextInt());
            }
        }
        return latinSquare;
    }

    public static void main(String[] args) {
        System.out.println(readTestCase("test_cases/d-15-01.txt"));
    }
}
