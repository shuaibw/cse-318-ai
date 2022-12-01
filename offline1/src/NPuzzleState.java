import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class NPuzzleState {
    private final int k;
    private final int[][] board;
    private ArrayList<NPuzzleState> neighbors = null;
    private NPuzzleState prev;
    private int distance = 0;
    public boolean visited;
    private Action action;

    public NPuzzleState(int k, int[][] n) {
        this.k = k;
        board = new int[k][k];
        for (int i = 0; i < k; i++) {
            System.arraycopy(n[i], 0, board[i], 0, k);
        }
    }

    public void printBoard() {
        StringBuilder sb = new StringBuilder("---------");
        sb.append("--------".repeat(Math.max(0, k - 1)));
        System.out.println("**" + sb.substring(2, sb.length() - 2) + "**");
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                System.out.print("|\t" + board[i][j] + "\t");
            }
            System.out.println("|");
            System.out.println(sb);
        }
    }

    public void printBoardClassic() {
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                System.out.print(board[i][j] + "\t");
            }
            System.out.println();
        }
    }

    public String getMove() {
        if (action == null) return "INITIAL";
        return action.name();
    }

    public NPuzzleState getPrev() {
        return prev;
    }

    public void setPrev(NPuzzleState prev) {
        this.prev = prev;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public ArrayList<NPuzzleState> getNeighbors() {
        if (neighbors != null) return neighbors;
        neighbors = new ArrayList<>();
        int[] zeroPos = findZero();
        int zeroRow = zeroPos[0];
        int zeroCol = zeroPos[1];
        if (zeroRow > 0) {
            addNeighbor(-1, 0, zeroRow, zeroCol, neighbors, Action.UP);
        }
        if (zeroRow < k - 1) {
            addNeighbor(1, 0, zeroRow, zeroCol, neighbors, Action.DOWN);
        }
        if (zeroCol > 0) {
            addNeighbor(0, -1, zeroRow, zeroCol, neighbors, Action.LEFT);
        }
        if (zeroCol < k - 1) {
            addNeighbor(0, 1, zeroRow, zeroCol, neighbors, Action.RIGHT);
        }
        return neighbors;
    }

    private void addNeighbor(int row, int col, int zeroRow, int zeroCol, ArrayList<NPuzzleState> neighbors, Action action) {
        int temp = board[zeroRow + row][zeroCol + col];
        board[zeroRow][zeroCol] = temp;
        board[zeroRow + row][zeroCol + col] = 0;
        NPuzzleState n = new NPuzzleState(k, board);
        n.action = action;
        neighbors.add(n);
        //restore board to previous state
        board[zeroRow + row][zeroCol + col] = temp;
        board[zeroRow][zeroCol] = 0;
    }

    private int[] findZero() {
        int[] zeroPos = new int[2];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                if (board[i][j] == 0) {
                    zeroPos[0] = i;
                    zeroPos[1] = j;
                    return zeroPos;
                }
            }
        }
        return zeroPos;
    }

    public int getHammingDistance() {
        int distance = 0;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                if (board[i][j] != i * k + j + 1 && board[i][j] != 0) {
                    distance++;
                }
            }
        }
        return distance;
    }

    public int getManhattanDistance() {
        int distance = 0;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                if (board[i][j] != i * k + j + 1 && board[i][j] != 0) {
                    int correctRow = (board[i][j] - 1) / k;
                    int correctCol = (board[i][j] - 1) % k;
                    distance += Math.abs(correctRow - i) + Math.abs(correctCol - j);
                }
            }
        }
        return distance;
    }

    public boolean solvable() {
        int[] arr = new int[k * k];
        int index = 0;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                arr[index++] = board[i][j];
            }
        }
        int inversions = 0;
        for (int i = 0; i < arr.length; i++) {
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[i] != 0 && arr[j] != 0 && arr[i] > arr[j]) {
                    inversions++;
                }
            }
        }
        if (k % 2 != 0) return inversions % 2 == 0;
        int[] zeroPos = findZero();
        int zeroRow = zeroPos[0];
        return (k - zeroRow) % 2 != inversions % 2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NPuzzleState that = (NPuzzleState) o;

        return Arrays.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    public static void main(String[] args) {
        // create a 3x3 board with values in declaration
        int[][] board = {
                {132, 2, 3},
                {4, 0, 6},
                {7, 832, 5}
        };
        NPuzzleState state = new NPuzzleState(3, board);
        state.action = Action.INITIAL;
        state.printBoard();
        System.out.println("************************");
        for (NPuzzleState s : state.getNeighbors()) {
            System.out.println(s.action.name());
            s.printBoard();
            System.out.println("************************");
        }
        System.out.println("Original board: ");
        state.printBoard();
    }
}
