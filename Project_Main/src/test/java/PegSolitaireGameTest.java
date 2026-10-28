import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

public class PegSolitaireGameTest {

    @Test
    void englishBoardStartsWithCenterEmptyAndContainsClassicCrossShape() {
        PegSolitaireGame game = new PegSolitaireGame(7, BoardType.ENGLISH);
        int center = 3;

        assertFalse(game.isPegAt(center, center));
        assertTrue(game.isCellPlayable(center, center));
        assertTrue(game.isCellPlayable(0, center));
        assertTrue(game.isCellPlayable(center, 0));
        assertTrue(game.isCellPlayable(1, 3));
        assertFalse(game.isCellPlayable(0, 0));
        assertTrue(game.getPegCount() > 0);
    }

    @Test
    void validJumpMovesPegAndRemovesJumpedPeg() throws Exception {
        PegSolitaireGame game = new PegSolitaireGame(7, BoardType.ENGLISH);
        Field boardField = PegSolitaireGame.class.getDeclaredField("board");
        boardField.setAccessible(true);
        boolean[][] board = (boolean[][]) boardField.get(game);

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = false;
            }
        }

        board[2][1] = true;
        board[2][2] = true;
        board[2][3] = false;

        assertTrue(game.makeMove(2, 1, 2, 3));
        assertFalse(game.isPegAt(2, 1));
        assertFalse(game.isPegAt(2, 2));
        assertTrue(game.isPegAt(2, 3));
    }

    @Test
    void invalidMoveIsRejected() {
        PegSolitaireGame game = new PegSolitaireGame(7, BoardType.ENGLISH);

        assertFalse(game.makeMove(0, 0, 0, 2));
        assertFalse(game.makeMove(0, 3, 0, 5));
    }

    @Test
    void invalidBoardSizeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new PegSolitaireGame(4, BoardType.ENGLISH));
    }

    @Test
    void gameIsOverWhenNoMovesRemain() throws Exception {
        PegSolitaireGame game = new PegSolitaireGame(7, BoardType.ENGLISH);

        Field boardField = PegSolitaireGame.class.getDeclaredField("board");
        boardField.setAccessible(true);
        boolean[][] board = (boolean[][]) boardField.get(game);

        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[row].length; col++) {
                board[row][col] = false;
            }
        }

        board[0][0] = true;
        assertTrue(game.isGameOver());
        assertTrue(game.getAvailableMoves().isEmpty());
    }
}
