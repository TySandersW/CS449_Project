import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

public class SolitaireGUI {
    private enum BoardType {
        ENGLISH;

        public boolean isCellActive(int row, int col, int boardSize) {
            if (row < 0 || row >= boardSize || col < 0 || col >= boardSize) {
                return false;
            }

            int center = boardSize / 2;
            return Math.abs(row - center) <= 1 || Math.abs(col - center) <= 1;
        }
    }

    private static final int MIN_SIZE = 5;
    private static final int MAX_SIZE = 11;
    private static final int DEFAULT_SIZE = 7;

    private int boardSize;
    private BoardType boardType;
    private boolean[][] board;
    private JButton[][] cells;
    private JLabel statusLabel;
    private JPanel boardPanel;
    private List<int[]> validMoves;
    private int[] selectedPeg;

    public static void createAndShowGUI() {
        new SolitaireGUI(DEFAULT_SIZE);
    }

    public SolitaireGUI(int boardSize) {
        this.boardSize = boardSize;
        this.boardType = BoardType.ENGLISH;
        initializeBoard();
        createAndShowFrame();
    }

    private void initializeBoard() {
        board = new boolean[boardSize][boardSize];
        cells = new JButton[boardSize][boardSize];
        validMoves = new ArrayList<>();
        selectedPeg = null;

        int center = boardSize / 2;
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (boardType.isCellActive(row, col, boardSize) && !(row == center && col == center)) {
                    board[row][col] = true;
                }
            }
        }
    }

    private void createAndShowFrame() {
        JFrame frame = new JFrame("Peg Solitaire");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        controls.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        JLabel sizeLabel = new JLabel("Board size:");
        JSpinner sizeSpinner = new JSpinner(new SpinnerNumberModel(DEFAULT_SIZE, MIN_SIZE, MAX_SIZE, 2));
        sizeSpinner.setPreferredSize(new Dimension(70, 28));

        JButton resetButton = new JButton("Reset Board");
        statusLabel = new JLabel("Select a peg to jump into the empty center.");
        statusLabel.setHorizontalAlignment(SwingConstants.LEFT);

        controls.add(sizeLabel);
        controls.add(sizeSpinner);
        controls.add(resetButton);

        boardPanel = new JPanel(new GridLayout(boardSize, boardSize, 6, 6));
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        boardPanel.setBackground(new Color(32, 32, 32));

        rebuildBoardGrid();

        resetButton.addActionListener(e -> {
            this.boardSize = (Integer) sizeSpinner.getValue();
            initializeBoard();
            rebuildBoardGrid();
        });

        sizeSpinner.addChangeListener(e -> {
            this.boardSize = (Integer) sizeSpinner.getValue();
            initializeBoard();
            rebuildBoardGrid();
        });

        frame.add(controls, BorderLayout.NORTH);
        frame.add(boardPanel, BorderLayout.CENTER);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void rebuildBoardGrid() {
        boardPanel.removeAll();
        boardPanel.setLayout(new GridLayout(boardSize, boardSize, 6, 6));

        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                JButton cell = new JButton();
                cell.setPreferredSize(new Dimension(55, 55));
                cell.setFocusPainted(false);
                cell.setOpaque(true);
                cell.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1));
                cell.setBackground(new Color(230, 230, 230));
                cell.setForeground(Color.BLACK);

                if (isOffBoardCell(row, col)) {
                    cell.setVisible(false);
                    cell.setEnabled(false);
                } else if (board[row][col]) {
                    cell.setBackground(new Color(255, 210, 90));
                    cell.setText("●");
                } else {
                    cell.setBackground(new Color(70, 70, 70));
                    cell.setText("○");
                }

                final int r = row;
                final int c = col;
                cell.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        handleCellClick(r, c);
                    }
                });

                cells[row][col] = cell;
                boardPanel.add(cell);
            }
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    private void handleCellClick(int row, int col) {
        if (isOffBoardCell(row, col)) {
            return;
        }

        if (selectedPeg != null && isValidMoveTarget(row, col)) {
            int startRow = selectedPeg[0];
            int startCol = selectedPeg[1];
            int middleRow = (startRow + row) / 2;
            int middleCol = (startCol + col) / 2;

            board[startRow][startCol] = false;
            board[middleRow][middleCol] = false;
            board[row][col] = true;

            selectedPeg = null;
            validMoves.clear();
            rebuildBoardGrid();
            statusLabel.setText("Move made. Jumped from (" + startRow + ", " + startCol + ") to (" + row + ", " + col + ").");

            if (!hasAnyPossibleMoves()) {
                showGameOverMessage();
            }
            return;
        }

        if (!board[row][col]) {
            statusLabel.setText("Choose a peg on the board to move.");
            return;
        }

        List<int[]> legalMoves = generateValidMovesForPeg(row, col);
        if (legalMoves.isEmpty()) {
            selectedPeg = null;
            validMoves.clear();
            statusLabel.setText("No legal jump from this peg.");
            rebuildBoardGrid();

            if (!hasAnyPossibleMoves()) {
                showGameOverMessage();
            }
            return;
        }

        selectedPeg = new int[]{row, col};
        validMoves.clear();
        validMoves.addAll(legalMoves);
        statusLabel.setText("Select a highlighted empty destination for the peg at (" + row + ", " + col + ").");
        highlightValidMoves();
    }

    private boolean hasAnyPossibleMoves() {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (!board[row][col] || isOffBoardCell(row, col)) {
                    continue;
                }
                if (!generateValidMovesForPeg(row, col).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void showGameOverMessage() {
        int remainingPegs = 0;
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (!isOffBoardCell(row, col) && board[row][col]) {
                    remainingPegs++;
                }
            }
        }

        String message = "Game over!\nRemaining pegs: " + remainingPegs + "\n";
        if (remainingPegs <= 2) {
            message += "Excellent work! You played brilliantly.";
        } else if (remainingPegs <= 6) {
            message += "Nice job! You did really well.";
        } else {
            message += "Better luck next time! Keep practicing.";
        }

        JButton resetButton = new JButton("Reset Board");
        resetButton.addActionListener(e -> {
            initializeBoard();
            rebuildBoardGrid();
            statusLabel.setText("Board reset. New game started.");
            JOptionPane.getRootFrame().dispose();
        });

        JOptionPane.showOptionDialog(
                boardPanel,
                message,
                "Game Over",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new Object[]{resetButton},
                resetButton);
    }

    private List<int[]> generateValidMovesForPeg(int row, int col) {
        List<int[]> moves = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            int nextRow = row + dir[0] * 2;
            int nextCol = col + dir[1] * 2;
            int middleRow = row + dir[0];
            int middleCol = col + dir[1];

            if (isInsideBoard(nextRow, nextCol) && !isOffBoardCell(nextRow, nextCol)
                    && isInsideBoard(middleRow, middleCol) && !isOffBoardCell(middleRow, middleCol)
                    && board[row][col] && !board[nextRow][nextCol] && board[middleRow][middleCol]) {
                moves.add(new int[]{nextRow, nextCol});
            }
        }

        return moves;
    }

    private void highlightValidMoves() {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                JButton cell = cells[row][col];
                if (cell == null || isOffBoardCell(row, col)) {
                    continue;
                }
                if (isValidMoveTarget(row, col)) {
                    cell.setBackground(new Color(140, 220, 160));
                    cell.setText("◎");
                } else if (board[row][col]) {
                    cell.setBackground(new Color(255, 210, 90));
                    cell.setText("●");
                } else {
                    cell.setBackground(new Color(70, 70, 70));
                    cell.setText("○");
                }
            }
        }
    }

    private boolean isValidMoveTarget(int row, int col) {
        for (int[] move : validMoves) {
            if (move[0] == row && move[1] == col) {
                return true;
            }
        }
        return false;
    }

    private boolean isOffBoardCell(int row, int col) {
        return !boardType.isCellActive(row, col, boardSize);
    }

    private boolean isInsideBoard(int row, int col) {
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize && !isOffBoardCell(row, col);
    }
}
