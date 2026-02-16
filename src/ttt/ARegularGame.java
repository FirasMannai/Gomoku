/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package ttt;

import java.util.List;

public abstract class ARegularGame<M> implements IRegularGame<M>, Cloneable {
    protected static final byte NONE = 0;
    protected static final byte ONE = 1;
    protected static final byte TWO = 2;

    protected final byte ROWS;
    protected final byte COLS;
    protected byte player;
    protected byte[][] board;
    protected int movesDone;
    protected byte lastRow, lastCol;

    public ARegularGame(byte rows, byte cols) {
        this.ROWS = rows;
        this.COLS = cols;
        this.player = ONE;               // Player 1 starts
        this.board = new byte[rows][cols];
        this.movesDone = 0;
        this.lastRow = -1;
        this.lastCol = -1;
    }

    @Override public byte getRows() { return ROWS; }
    @Override public byte getCols() { return COLS; }
    @Override public byte getPlayer1() { return ONE; }
    @Override public byte getPlayer2() { return TWO; }
    @Override public byte getPlayerNone() { return NONE; }

    @Override public byte currentPlayer() {
        return player;
    }

    @Override public byte otherPlayer(byte p) {
        return p == ONE ? TWO : ONE;
    }

    protected byte lastPlayer() {
        return otherPlayer(currentPlayer());
    }

    @Override public byte getAtPosition(byte row, byte col) {
        return board[row][col];
    }

    @Override public boolean endedGame() {
        return noMoreMove() || wins(lastPlayer());
    }

    @Override public ARegularGame<M> clone() throws CloneNotSupportedException {
        ARegularGame<M> copy = (ARegularGame<M>) super.clone();
        copy.board = new byte[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            System.arraycopy(this.board[r], 0, copy.board[r], 0, COLS);
        }
        copy.player = this.player;
        copy.movesDone = this.movesDone;
        copy.lastRow = this.lastRow;
        copy.lastCol = this.lastCol;
        return copy;
    }

    @Override public abstract List<M> moves();
    @Override public abstract IRegularGame<M> setAtPosition(byte row, byte col);
    @Override public abstract IRegularGame<M> doMove(M move);
    @Override public abstract boolean noMoreMove();
    @Override public abstract boolean wins(byte player);
    @Override public abstract int evalState(byte player);
}

