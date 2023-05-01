import java.util.ArrayList;

import static java.lang.Math.*;

public class StudentPlayer extends Player {
    private int enemyPlayer, DEPTH = 5;

    public StudentPlayer(int playerIndex, int[] boardSize, int nToConnect) {
        super(playerIndex, boardSize, nToConnect);
        enemyPlayer = (playerIndex == 1 ? 2 : 1);
    }

    private class Move {
        public int highestScore;
        public int bestMove;
        public int depth = 0;

        public Move(int newScore, int newMove) {
            highestScore = newScore;
            bestMove = newMove;
        }
    }

    private int checkRow(Board board, int nCheck) {
        int nInARow = 0, value = 0;
        int[][] state = board.getState();

        for (int r = 0; r < 6; r++) {
            for (int col = 0; col < 7; col++) {
                if (state[r][col] == playerIndex) {
                    nInARow++;
                    if (nInARow == nCheck) {
                        if (nCheck == nToConnect)
                            return Integer.MAX_VALUE;
                        boolean left = (col - nCheck >= 0 && state[r][col - nCheck] == 0);
                        boolean right = (col + 1 < 7 && state[r][col + 1] == 0);
                        if (left && right)
                            value += pow(10, nCheck) * 6;
                        else {
                            if (left)
                                value += 2*pow(10, nCheck);
                            if (right)
                                value += 2*pow(10, nCheck);
                        }
                    }
                } else
                    nInARow = 0;
            }
        }
        return value;
    }

    private int checkCol(Board board, int nCheck) {
        int nInACol = 0, value = 0;
        int[][] state = board.getState();

        for (int col = 0; col < 7; col++) {
            for (int r = 0; r < 6; r++) {
                if (state[r][col] == playerIndex) {
                    nInACol++;
                    if (nInACol == nCheck) {
                        if (nCheck == nToConnect)
                            return Integer.MAX_VALUE;
                        boolean above = (r - nCheck >= 0 && state[r - nCheck][col] == 0);
                            if (above)
                                value += pow(10, nCheck);
                    }
                } else
                    nInACol = 0;
            }
        }
        return value;
    }

    private int checkDiagonally(Board board, int nCheck) {
        int value = 0;
        int nInADiagonal = 0;
        int[][] state = board.getState();

        for (int col = 0; col < 7 - nCheck; col++) {
            for (int row = 0; row < 6 - nCheck; row++) {
                for (int d = 0; d < nCheck; d++) {
                    if (state[row + d][col + d] == playerIndex) {
                        nInADiagonal++;
                        if (nInADiagonal == nCheck)
                        {
                            if ((row + d + 1 < 6 && col + d + 1 < 7 && state[row + d + 1][col + d + 1] == 0)
                            || (row + d - nCheck >= 0 && col + d - nCheck >= 0 && state[row  + d - nCheck][col  + d - nCheck] == 0))
                            {
                                value += 3 * pow(10, nCheck);
                            }
                        }
                    } else {
                        nInADiagonal = 0;
                        d = nCheck;
                    }
                }
            }
        }
        return value;
    }

    private int checkSkewDiagonally(Board board, int nCheck) {
        int value = 0;
        int nInADiagonal = 0;
        int[][] state = board.getState();

        for (int col = 0; col < 7 - nCheck; col++) {
            for (int row = nCheck - 1; row < 6; row++) {
                for (int d = 0; d < nCheck; d++) {
                    if (state[row - d][col + d] == playerIndex) {
                        nInADiagonal++;
                        if (nInADiagonal == nCheck) {
                            if ((row - d - 1 >= 0 && col + d + 1 < 7 && state[row - d - 1][col + d + 1] == 0) ||
                                    (row - d + nCheck < 6 && col + d - nCheck > 0 && state[row - d + nCheck][col + d - nCheck] == 0)) {
                                value += 3 * pow(10, nCheck);
                            }
                        }
                    } else {
                        nInADiagonal = 0;
                        d = nCheck;
                    }
                }
            }
        }
        return value;
    }

    int[][] values = {
            {1, 4, 5, 7, 5, 4, 1},
            {4, 6, 8, 10, 8, 6, 4},
            {5, 8, 11, 13, 11, 8, 5},
            {5, 8, 11, 13, 11, 8, 5},
            {4, 6, 8, 10, 8, 6, 4},
            {1, 4, 5, 7, 5, 4, 1}
    };

    private int evaluate(Board board) {
        int value = 0;
        value += checkRow(board, 3);
        value += checkCol(board, 3);
        value += checkRow(board, 2);
        value += checkCol(board, 2);
        value += checkDiagonally(board, 3);
        value += checkSkewDiagonally(board, 3);
        value += checkDiagonally(board, 2);
        value += checkSkewDiagonally(board, 2);

        int[][] state = board.getState();
        for (int row = 0; row < 6; row++){
            for (int col = 0; col < 7; col++){
                if (state[row][col] == playerIndex)
                    value += values[row][col];
            }
        }
            return value;
    }

    public Move miniMax(Board board, boolean isMax, int depth) {
        ArrayList<Integer> validCols = board.getValidSteps();
        if (board.gameEnded()) {
            if (board.getWinner() == playerIndex) {
                Move m = new Move(Integer.MAX_VALUE, -1);
                m.depth = depth;
                return m;
            } else if (board.getWinner() == enemyPlayer) {
                Move m = new Move(Integer.MIN_VALUE, -1);
                m.depth = depth;
                return m;
            } else {
                return new Move(0, -1);
            }
        }
        if (depth == 0)
            return new Move(evaluate(board), 0);
        Move best = new Move(0, validCols.get(0));
        if (isMax) {
            best.highestScore = Integer.MIN_VALUE;
            for (int col : validCols) {
                Board boardCopy = new Board(board);
                boardCopy.step(playerIndex, col);
                Move move = miniMax(boardCopy, false, depth - 1);

                if (best.highestScore == 0 && move.highestScore == 0) {
                    if ((best.bestMove == 0 || best.bestMove == 6) && (move.bestMove != 0 && move.bestMove != 6)) {
                        best.highestScore = move.highestScore;
                        best.bestMove = col;
                    }
                }
                if (move.highestScore > best.highestScore) {
                    best.highestScore = move.highestScore;
                    best.bestMove = col;
                } else if (move.highestScore == best.highestScore && move.depth > best.depth) {
                    best.highestScore = move.highestScore;
                    best.bestMove = col;
                    best.depth = move.depth;
                }
            }
        } else {
            best.highestScore = Integer.MAX_VALUE;
            for (int col : validCols) {
                Board boardCopy = new Board(board);
                boardCopy.step(enemyPlayer, col);
                Move move = miniMax(boardCopy, true, depth - 1);

                if (move.highestScore < best.highestScore) {
                    best.highestScore = move.highestScore;
                    best.bestMove = col;
                } else if (move.highestScore == best.highestScore && move.depth < best.depth) {
                    best.highestScore = move.highestScore;
                    best.bestMove = col;
                    best.depth = move.depth;
                }
            }
        }
        return best;
    }

    @Override
    public int step(Board board) {
        return miniMax(board, true, DEPTH).bestMove;
    }

}