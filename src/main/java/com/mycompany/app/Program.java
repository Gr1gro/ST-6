package com.mycompany.app;

import java.util.ArrayList;
import java.util.List;

enum GameState {
    PLAYING,
    X_WON,
    O_WON,
    DRAW
}

final class Move {
    private final int row;
    private final int col;

    Move(int row, int col) {
        this.row = row;
        this.col = col;
    }

    int getRow() {
        return row;
    }

    int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Move)) {
            return false;
        }
        Move other = (Move) obj;
        return row == other.row && col == other.col;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}

class Game {
    static final char X = 'X';
    static final char O = 'O';
    static final char EMPTY = ' ';
    static final int SIZE = 3;

    private final char[][] board;
    private char currentPlayer;

    Game() {
        board = new char[SIZE][SIZE];
        reset();
    }

    void reset() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                board[row][col] = EMPTY;
            }
        }
        currentPlayer = X;
    }

    char getCurrentPlayer() {
        return currentPlayer;
    }

    char getCell(int row, int col) {
        if (!isInside(row, col)) {
            throw new IllegalArgumentException("Cell is outside the board");
        }
        return board[row][col];
    }

    boolean isEmptyCell(int row, int col) {
        return isInside(row, col) && board[row][col] == EMPTY;
    }

    boolean isValidMove(int row, int col) {
        return isInside(row, col) && board[row][col] == EMPTY && !isGameOver();
    }

    boolean placeMove(int row, int col) {
        if (!isValidMove(row, col)) {
            return false;
        }
        board[row][col] = currentPlayer;
        if (!isGameOver()) {
            switchPlayer();
        }
        return true;
    }

    void setCellForTest(int row, int col, char player) {
        if (!isInside(row, col)) {
            throw new IllegalArgumentException("Cell is outside the board");
        }
        if (player != X && player != O && player != EMPTY) {
            throw new IllegalArgumentException("Unknown player");
        }
        board[row][col] = player;
    }

    void setCurrentPlayerForTest(char player) {
        if (player != X && player != O) {
            throw new IllegalArgumentException("Unknown player");
        }
        currentPlayer = player;
    }

    void switchPlayer() {
        currentPlayer = currentPlayer == X ? O : X;
    }

    Character getWinner() {
        if (hasWon(X)) {
            return X;
        }
        if (hasWon(O)) {
            return O;
        }
        return null;
    }

    GameState getState() {
        Character winner = getWinner();
        if (winner != null && winner == X) {
            return GameState.X_WON;
        }
        if (winner != null && winner == O) {
            return GameState.O_WON;
        }
        if (isFullBoard()) {
            return GameState.DRAW;
        }
        return GameState.PLAYING;
    }

    boolean isGameOver() {
        return getState() != GameState.PLAYING;
    }

    boolean isFullBoard() {
        return getAvailableMoves().isEmpty();
    }

    List<Move> getAvailableMoves() {
        List<Move> moves = new ArrayList<Move>();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (board[row][col] == EMPTY) {
                    moves.add(new Move(row, col));
                }
            }
        }
        return moves;
    }

    Move findBestMove(char aiPlayer) {
        if (isEmptyBoard() && isEmptyCell(1, 1)) {
            return new Move(1, 1);
        }

        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;
        for (Move move : getAvailableMoves()) {
            board[move.getRow()][move.getCol()] = aiPlayer;
            int score = minimax(false, aiPlayer);
            board[move.getRow()][move.getCol()] = EMPTY;
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }
        return bestMove;
    }

    int minimax(boolean maximizing, char aiPlayer) {
        GameState state = getState();
        if (state != GameState.PLAYING) {
            return scoreState(state, aiPlayer);
        }

        char player = maximizing ? aiPlayer : opponentOf(aiPlayer);
        int bestScore = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (Move move : getAvailableMoves()) {
            board[move.getRow()][move.getCol()] = player;
            int score = minimax(!maximizing, aiPlayer);
            board[move.getRow()][move.getCol()] = EMPTY;
            if (maximizing) {
                bestScore = Math.max(bestScore, score);
            } else {
                bestScore = Math.min(bestScore, score);
            }
        }
        return bestScore;
    }

    String boardToString() {
        StringBuilder result = new StringBuilder();
        for (int row = 0; row < SIZE; row++) {
            if (row > 0) {
                result.append('\n');
            }
            for (int col = 0; col < SIZE; col++) {
                if (col > 0) {
                    result.append('|');
                }
                result.append(board[row][col] == EMPTY ? '-' : board[row][col]);
            }
        }
        return result.toString();
    }

    private int scoreState(GameState state, char aiPlayer) {
        if ((state == GameState.X_WON && aiPlayer == X)
                || (state == GameState.O_WON && aiPlayer == O)) {
            return 10;
        }
        if ((state == GameState.X_WON && aiPlayer == O)
                || (state == GameState.O_WON && aiPlayer == X)) {
            return -10;
        }
        return 0;
    }

    private boolean hasWon(char player) {
        for (int index = 0; index < SIZE; index++) {
            if (board[index][0] == player && board[index][1] == player
                    && board[index][2] == player) {
                return true;
            }
            if (board[0][index] == player && board[1][index] == player
                    && board[2][index] == player) {
                return true;
            }
        }
        return (board[0][0] == player && board[1][1] == player
                && board[2][2] == player)
                || (board[0][2] == player && board[1][1] == player
                && board[2][0] == player);
    }

    private boolean isInside(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    private boolean isEmptyBoard() {
        return getAvailableMoves().size() == SIZE * SIZE;
    }

    private char opponentOf(char player) {
        return player == X ? O : X;
    }
}

public class Program {
    public static void main(String[] args) {
        Game game = new Game();
        game.placeMove(0, 0);
        Move move = game.findBestMove(Game.O);

        if (move != null) {
            game.placeMove(move.getRow(), move.getCol());
        }

        System.out.println("Current board:");
        System.out.println(game.boardToString());
        System.out.println("State: " + game.getState());
    }
}
