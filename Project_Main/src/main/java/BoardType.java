public enum BoardType {
    ENGLISH,
    HEXAGON,
    DIAMOND;

    public boolean isCellActive(int row, int col, int boardSize) {
        if (row < 0 || row >= boardSize || col < 0 || col >= boardSize) {
            return false;
        }

        int center = boardSize / 2;

        if (this == ENGLISH) {
            return Math.abs(row - center) <= 1 || Math.abs(col - center) <= 1;
        }

        switch (this) {
            case HEXAGON:
                int dx = Math.abs(col - center);
                int dy = Math.abs(row - center);
                int maxDistance = Math.max(dx, dy);
                int antiDiagonal = Math.abs((row + col) - (2 * center));
                return Math.max(maxDistance, antiDiagonal) <= boardSize / 2;
            case DIAMOND:
                return Math.abs(row - center) + Math.abs(col - center) <= boardSize / 2;
            default:
                return false;
        }
    }
}