/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ttt;

import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

public class Gomoku extends ARegularGame<Pair<Byte, Byte>> {
    public static final byte DEFAULT_SIZE = 15;

    // Precomputed center-closeness weights for each board position
    private static final int[][] CENTER_WEIGHTS = createCenterWeights(DEFAULT_SIZE);

    // Store the five winning stones (if game is won)
    private List<Pair<Byte, Byte>> winningStones = new ArrayList<>();

    public Gomoku() {
        this(DEFAULT_SIZE, DEFAULT_SIZE);
    }

    /** Constructor: creates a rows×cols board. */
    public Gomoku(byte rows, byte cols) {
        super(rows, cols);
    }

    private static int[][] createCenterWeights(int size) {
        int[][] weights = new int[size][size];
        int center = size / 2;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                weights[r][c] = size - Math.max(Math.abs(r - center), Math.abs(c - center));
            }
        }
        return weights;
    }
    
    //Evaluates board score for a single position of the given player
    public int evalStateForPosition(byte player, byte row, byte col) {
        int score = CENTER_WEIGHTS[row][col];
        int[][] directions = {
            { 0, 1 },  // horizontal
            { 1, 0 },  // vertical
            { 1, 1 },  // diagonal ↘
            { 1, -1 }  // diagonal ↗
        };
        for (int[] dir : directions) {
            score += evaluatePotentialSequence(player, row, col, dir[0], dir[1]);
        }
        return score;
    }

    private int evaluatePotentialSequence(byte player, int r, int c, int dr, int dc) {
        int playerCount = 0;
        int emptyCount = 0;
        for (int i = -4; i <= 4; i++) {
            if (i == 0) continue;
            int nr = r + dr * i;
            int nc = c + dc * i;
            if (nr < 0 || nr >= ROWS || nc < 0 || nc >= COLS) break;
            byte occupant = board[nr][nc];
            if (occupant == player) {
                playerCount++;
            } else if (occupant == NONE) {
                emptyCount++;
            } else {
                break;
            }
        }
        if (playerCount >= 3 && emptyCount >= 2) return 100;
        if (playerCount >= 2 && emptyCount >= 3) return 50;
        if (playerCount >= 1) return 10;
        return 0;
    }
    
    //Evaluates the game state from the perspective of the given player.
    @Override
    public int evalState(byte player) {
        if (wins(player)) return Integer.MAX_VALUE;
        byte opponent = otherPlayer(player);
        if (wins(opponent)) return Integer.MIN_VALUE;

        int score = 0;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                byte occupant = board[r][c];
                if (occupant == player) {
                    score += evalStateForPosition(player, (byte) r, (byte) c);
                } else if (occupant == opponent) {
                    score -= evalStateForPosition(opponent, (byte) r, (byte) c);
                }
            }
        }
        return score;
    }
    //Generates all valid moves near already placed stones.
    @Override
    public List<Pair<Byte, Byte>> moves() {
        if (movesDone == 0) {
            byte mid = (byte)(ROWS / 2); 
            return List.of(new Pair<>(mid, mid));
        }
        Set<Pair<Byte, Byte>> candidates = new HashSet<>();
        for (byte r = 0; r < ROWS; r++) {
            for (byte c = 0; c < COLS; c++) {
                if (board[r][c] != NONE) {
                    for (int dr = -1; dr <= 1; dr++) {
                        for (int dc = -1; dc <= 1; dc++) {
                            int nr = r + dr, nc = c + dc;
                            if (nr >= 0 && nr < ROWS && nc >= 0 && nc < COLS) {
                                if (board[nr][nc] == NONE) {
                                    candidates.add(new Pair<>((byte) nr, (byte) nc));
                                }
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>(candidates);
    }
    
    //Places a stone at the given position if valid
    @Override
    public IRegularGame<Pair<Byte, Byte>> setAtPosition(byte row, byte col) {
        if (row < 0 || row >= ROWS || col < 0 || col >= COLS || board[row][col] != NONE) {
            return this; // invalid move
        }
        return doMove(new Pair<>(row, col));
    }

    @Override
    public IRegularGame<Pair<Byte, Byte>> doMove(Pair<Byte, Byte> move) {
        try {
            Gomoku copy = (Gomoku) this.clone();
            byte current = copy.currentPlayer();
            copy.board[move.first][move.second] = current;
            copy.lastRow = move.first;
            copy.lastCol = move.second;
            copy.movesDone++;
            copy.player = otherPlayer(current);
            // No need to update winningStones here (will be set by wins())
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Clone failed", e);
        }
    }
    
    //Checks if the board is full
    @Override
    public boolean noMoreMove() {
        return movesDone >= ROWS * COLS;
    }
    
    //Checks if the specified player has a winning sequence of 5 stones.
    @Override
    public boolean wins(byte player) {
        winningStones.clear();
        int[][] directions = {
            { 1,  0},  // vertical
            { 0,  1},  // horizontal
            { 1,  1},  // diagonal ↘
            { 1, -1}   // diagonal ↗
        };
        for (int[] d : directions) {
            List<Pair<Byte, Byte>> stones = new ArrayList<>();
            stones.add(new Pair<>(lastRow, lastCol));
            // Forward
            for (int step = 1; step < 5; step++) {
                int nr = lastRow + d[0] * step;
                int nc = lastCol + d[1] * step;
                if (nr < 0 || nr >= ROWS || nc < 0 || nc >= COLS) break;
                if (board[nr][nc] != player) break;
                stones.add(new Pair<>((byte) nr, (byte) nc));
            }
            // Backward
            for (int step = 1; step < 5; step++) {
                int nr = lastRow - d[0] * step;
                int nc = lastCol - d[1] * step;
                if (nr < 0 || nr >= ROWS || nc < 0 || nc >= COLS) break;
                if (board[nr][nc] != player) break;
                stones.add(0, new Pair<>((byte) nr, (byte) nc));
            }
            if (stones.size() >= 5) {
                winningStones = stones.subList(0, 5);
                return true;
            }
        }
        return false;
    }

    /** Returns a copy of the 5 winning stones (if any). */
    public List<Pair<Byte, Byte>> getWinningStones() {
        return new ArrayList<>(winningStones);
    }
}







