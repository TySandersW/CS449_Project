import java.util.ArrayList;
import java.util.List;

public class PegSolitaireGame {
    private int boardSize;
    private BoardType boardType;
    private boolean[][] board;

    public PegSolitaireGame(int boardSize, BoardType boardType) {
        startNewGame(boardSize, boardType);
    }

    public void startNewGame(int boardSize, BoardType boardType) {
        if (boardSize < 3 || boardSize % 2 == 0) {
            throw new IllegalArgumentException("Board size must be an odd number greater than or equal to 3.");
        }

        this.boardSize = boardSize;
        this.boardType = boardType;
        this.board = new boolean[boardSize][boardSize];

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (boardType.isCellActive(row, col, boardSize)) {
                    board[row][col] = true;
                }
            }
        }

        int center = boardSize / 2;
        board[center][center] = false;
    }

    public boolean isPegAt(int row, int col) {
        return isInsideBoard(row, col) && board[row][col];
    }

    public boolean isCellPlayable(int row, int col) {
        return boardType.isCellActive(row, col, boardSize);
    }

    public boolean[][] getBoardCopy() {
        boolean[][] copy = new boolean[boardSize][boardSize];
        for (int row = 0; row < boardSize; row++) {
            System.arraycopy(board[row], 0, copy[row], 0, boardSize);
        }
        return copy;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public BoardType getBoardType() {
        return boardType;
    }

    public int getPegCount() {
        int count = 0;
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col]) {
                    count++;
                }
            }
        }
        return count;
    }

    public List<Move> getAvailableMoves() {
        List<Move> moves = new ArrayList<>();

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (!isPegAt(row, col)) {
                    continue;
                }
                for (Move move : getMovesFromPeg(row, col)) {
                    moves.add(move);
                }
            }
        }

        return moves;
    }

    public boolean isGameOver() {
        return getAvailableMoves().isEmpty();
    }

    public boolean makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (!isPegAt(fromRow, fromCol) || !isInsideBoard(fromRow, fromCol)) {
            return false;
        }

        if (!isInsideBoard(toRow, toCol) || !isCellPlayable(toRow, toCol) || isPegAt(toRow, toCol)) {
            return false;
        }

        int rowDelta = Math.abs(toRow - fromRow);
        int colDelta = Math.abs(toCol - fromCol);
        if ((rowDelta != 2 || colDelta != 0) && (rowDelta != 0 || colDelta != 2)) {
            return false;
        }

        int midRow = (fromRow + toRow) / 2;
        int midCol = (fromCol + toCol) / 2;
        if (!isPegAt(midRow, midCol) || !isCellPlayable(midRow, midCol)) {
            return false;
        }

        board[fromRow][fromCol] = false;
        board[midRow][midCol] = false;
        board[toRow][toCol] = true;
        return true;
    }

    public boolean makeMove(Move move) {
        return makeMove(move.fromRow(), move.fromCol(), move.toRow(), move.toCol());
    }

    private List<Move> getMovesFromPeg(int row, int col) {
        List<Move> moves = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] direction : directions) {
            int toRow = row + direction[0] * 2;
            int toCol = col + direction[1] * 2;
            int midRow = row + direction[0];
            int midCol = col + direction[1];

            if (isInsideBoard(toRow, toCol) && isCellPlayable(toRow, toCol)
                    && !isPegAt(toRow, toCol) && isPegAt(midRow, midCol)) {
                moves.add(new Move(row, col, toRow, toCol));
            }
        }

        return moves;
    }

    private boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
    }

    public record Move(int fromRow, int fromCol, int toRow, int toCol) {
    }
}
