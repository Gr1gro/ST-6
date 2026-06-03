package com.mycompany.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class ProgramTest {
    @Test
    void initialBoardIsEmpty() {
        Game game = new Game();

        for (int row = 0; row < Game.SIZE; row++) {
            for (int col = 0; col < Game.SIZE; col++) {
                assertEquals(Game.EMPTY, game.getCell(row, col));
            }
        }
    }

    @Test
    void initialPlayerIsX() {
        Game game = new Game();

        assertEquals(Game.X, game.getCurrentPlayer());
    }

    @Test
    void validMoveIsAccepted() {
        Game game = new Game();

        assertTrue(game.placeMove(0, 0));
        assertEquals(Game.X, game.getCell(0, 0));
    }

    @Test
    void invalidMoveOutsideBoardIsRejected() {
        Game game = new Game();

        assertFalse(game.placeMove(-1, 0));
        assertFalse(game.placeMove(0, 3));
    }

    @Test
    void invalidMoveOnOccupiedCellIsRejected() {
        Game game = new Game();

        assertTrue(game.placeMove(0, 0));
        assertFalse(game.placeMove(0, 0));
    }

    @Test
    void playerSwitchesAfterValidMove() {
        Game game = new Game();

        game.placeMove(0, 0);

        assertEquals(Game.O, game.getCurrentPlayer());
    }

    @Test
    void playerDoesNotSwitchAfterInvalidMove() {
        Game game = new Game();

        game.placeMove(-1, 0);

        assertEquals(Game.X, game.getCurrentPlayer());
    }

    @Test
    void xWinsByRow() {
        Game game = new Game();
        setRow(game, 0, Game.X);

        assertEquals(GameState.X_WON, game.getState());
    }

    @Test
    void oWinsByRow() {
        Game game = new Game();
        setRow(game, 2, Game.O);

        assertEquals(GameState.O_WON, game.getState());
    }

    @Test
    void xWinsByColumn() {
        Game game = new Game();
        setColumn(game, 1, Game.X);

        assertEquals(GameState.X_WON, game.getState());
    }

    @Test
    void oWinsByColumn() {
        Game game = new Game();
        setColumn(game, 2, Game.O);

        assertEquals(GameState.O_WON, game.getState());
    }

    @Test
    void xWinsByMainDiagonal() {
        Game game = new Game();
        game.setCellForTest(0, 0, Game.X);
        game.setCellForTest(1, 1, Game.X);
        game.setCellForTest(2, 2, Game.X);

        assertEquals(GameState.X_WON, game.getState());
    }

    @Test
    void oWinsByOtherDiagonal() {
        Game game = new Game();
        game.setCellForTest(0, 2, Game.O);
        game.setCellForTest(1, 1, Game.O);
        game.setCellForTest(2, 0, Game.O);

        assertEquals(GameState.O_WON, game.getState());
    }

    @Test
    void drawIsDetected() {
        Game game = drawGame();

        assertEquals(GameState.DRAW, game.getState());
    }

    @Test
    void gameOverAfterWin() {
        Game game = new Game();
        setRow(game, 0, Game.X);

        assertTrue(game.isGameOver());
    }

    @Test
    void gameOverAfterDraw() {
        Game game = drawGame();

        assertTrue(game.isGameOver());
    }

    @Test
    void gameNotOverAtStart() {
        Game game = new Game();

        assertFalse(game.isGameOver());
    }

    @Test
    void boardToStringReturnsExpectedContent() {
        Game game = new Game();
        game.placeMove(0, 0);

        assertEquals("X|-|-\n-|-|-\n-|-|-", game.boardToString());
    }

    @Test
    void minimaxScoresWinningPositionCorrectly() {
        Game game = new Game();
        setRow(game, 0, Game.X);

        assertEquals(10, game.minimax(true, Game.X));
    }

    @Test
    void minimaxScoresLosingPositionCorrectly() {
        Game game = new Game();
        setRow(game, 0, Game.X);

        assertEquals(-10, game.minimax(true, Game.O));
    }

    @Test
    void minimaxScoresDrawPositionCorrectly() {
        Game game = drawGame();

        assertEquals(0, game.minimax(true, Game.X));
    }

    @Test
    void bestMoveBlocksOpponentWin() {
        Game game = new Game();
        game.setCellForTest(0, 0, Game.X);
        game.setCellForTest(0, 1, Game.X);
        game.setCellForTest(1, 1, Game.O);

        assertEquals(new Move(0, 2), game.findBestMove(Game.O));
    }

    @Test
    void bestMoveWinsImmediatelyWhenPossible() {
        Game game = new Game();
        game.setCellForTest(0, 0, Game.O);
        game.setCellForTest(0, 1, Game.O);
        game.setCellForTest(1, 1, Game.X);

        assertEquals(new Move(0, 2), game.findBestMove(Game.O));
    }

    @Test
    void bestMoveChoosesCenterOnEmptyBoard() {
        Game game = new Game();

        assertEquals(new Move(1, 1), game.findBestMove(Game.O));
    }

    @Test
    void availableMovesCountIsCorrect() {
        Game game = new Game();
        game.placeMove(0, 0);
        game.placeMove(1, 1);

        assertEquals(7, game.getAvailableMoves().size());
    }

    @Test
    void resetBoardClearsMovesAndRestoresCurrentPlayer() {
        Game game = new Game();
        game.placeMove(0, 0);
        game.reset();

        assertEquals(Game.EMPTY, game.getCell(0, 0));
        assertEquals(Game.X, game.getCurrentPlayer());
    }

    @Test
    void currentPlayerGetterWorks() {
        Game game = new Game();
        game.switchPlayer();

        assertEquals(Game.O, game.getCurrentPlayer());
    }

    @Test
    void winnerGetterReturnsWinner() {
        Game game = new Game();
        setColumn(game, 0, Game.O);

        assertEquals(Character.valueOf(Game.O), game.getWinner());
    }

    @Test
    void winnerGetterReturnsNullAtStart() {
        Game game = new Game();

        assertNull(game.getWinner());
    }

    @Test
    void emptyCellCheckWorks() {
        Game game = new Game();

        assertTrue(game.isEmptyCell(2, 2));
        game.placeMove(2, 2);
        assertFalse(game.isEmptyCell(2, 2));
    }

    @Test
    void fullBoardCheckWorks() {
        Game game = drawGame();

        assertTrue(game.isFullBoard());
    }

    @Test
    void invalidGetCellThrows() {
        Game game = new Game();

        assertThrows(IllegalArgumentException.class, () -> game.getCell(5, 0));
    }

    @Test
    void setCellRejectsUnknownPlayer() {
        Game game = new Game();

        assertThrows(IllegalArgumentException.class,
            () -> game.setCellForTest(0, 0, 'A'));
    }

    @Test
    void setCurrentPlayerRejectsUnknownPlayer() {
        Game game = new Game();

        assertThrows(IllegalArgumentException.class,
            () -> game.setCurrentPlayerForTest('B'));
    }

    @Test
    void setCurrentPlayerAcceptsO() {
        Game game = new Game();
        game.setCurrentPlayerForTest(Game.O);

        assertEquals(Game.O, game.getCurrentPlayer());
    }

    @Test
    void moveGettersReturnCoordinates() {
        Move move = new Move(1, 2);

        assertEquals(1, move.getRow());
        assertEquals(2, move.getCol());
    }

    @Test
    void moveToStringContainsCoordinates() {
        Move move = new Move(1, 2);

        assertEquals("(1, 2)", move.toString());
    }

    @Test
    void moveEqualsRejectsDifferentType() {
        Move move = new Move(1, 2);

        assertFalse(move.equals("not a move"));
    }

    @Test
    void equalMovesHaveSameHashCode() {
        Move first = new Move(2, 1);
        Move second = new Move(2, 1);

        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    void noBestMoveExistsWhenBoardIsFull() {
        Game game = drawGame();

        assertNull(game.findBestMove(Game.X));
    }

    @Test
    void availableMovesContainExpectedMove() {
        Game game = new Game();
        List<Move> moves = game.getAvailableMoves();

        assertTrue(moves.contains(new Move(2, 2)));
    }

    @Test
    void mainRunsDemoWithoutError() {
        Program.main(new String[0]);

        assertTrue(true);
    }

    @Test
    void bestMoveReturnsSomeMoveForNonTerminalBoard() {
        Game game = new Game();
        game.placeMove(0, 0);

        assertNotNull(game.findBestMove(Game.O));
    }

    private static void setRow(Game game, int row, char player) {
        for (int col = 0; col < Game.SIZE; col++) {
            game.setCellForTest(row, col, player);
        }
    }

    private static void setColumn(Game game, int col, char player) {
        for (int row = 0; row < Game.SIZE; row++) {
            game.setCellForTest(row, col, player);
        }
    }

    private static Game drawGame() {
        Game game = new Game();
        char[][] board = {
            {Game.X, Game.O, Game.X},
            {Game.X, Game.O, Game.O},
            {Game.O, Game.X, Game.X}
        };
        for (int row = 0; row < Game.SIZE; row++) {
            for (int col = 0; col < Game.SIZE; col++) {
                game.setCellForTest(row, col, board[row][col]);
            }
        }
        return game;
    }
}
