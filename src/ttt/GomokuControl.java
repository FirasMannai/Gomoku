/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package ttt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GomokuControl<M> extends JPanel {
    protected GomokuBoard<M> board;
    private final IGameKI<M> ai1;
    private final IGameKI<M> ai2;
    private final String mode;
    private Timer ccTimer;

    private int blackWins = 0;
    private int whiteWins = 0;
    private final JLabel winLabel = new JLabel("Black: 0  Red: 0", SwingConstants.CENTER);

    // --- Undo support (PC mode only) ---
    private final JButton undoButton = new JButton("Undo");
    private final List<IRegularGame<M>> history = new ArrayList<>();

    public GomokuControl(IRegularGame<M> game, IGameKI<M> ai, String mode) {
        this(game, ai, ai, mode);
    }

    public GomokuControl(IRegularGame<M> game, IGameKI<M> ai1, IGameKI<M> ai2, String mode) {
        this.board = new GomokuBoard<>(game);
        this.ai1 = ai1;
        this.ai2 = ai2;
        this.mode = mode;

        // ---- Layout and top panel ----
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.setBackground(new Color(222, 184, 135)); // Same as board!

        winLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        winLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        topPanel.add(Box.createHorizontalGlue()); // push center
        topPanel.add(winLabel);
        topPanel.add(Box.createHorizontalGlue());

        if (mode.equals("PC")) {
            undoButton.addActionListener(e -> undoTwoMoves());
            undoButton.setFocusable(false);
            undoButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
            undoButton.setBackground(new Color(120, 92, 50)); // Nice brown
            undoButton.setForeground(Color.WHITE);
            undoButton.setMargin(new Insets(6, 24, 6, 24));
            topPanel.add(undoButton);
            history.clear();
            history.add(cloneGame(game));
        }

        add(topPanel, BorderLayout.NORTH);
        add(board, BorderLayout.CENTER);

        if (Debugger.isDebug()) {
            Debugger.printMoveHeader();
        }

        // Mouse listener only for human players
        if (!mode.equals("CC")) {
            board.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent ev) {
                    int c = (ev.getX() - GomokuBoard.MARGIN + GomokuBoard.CELL_SIZE / 2) / GomokuBoard.CELL_SIZE;
                    int r = (ev.getY() - GomokuBoard.MARGIN + GomokuBoard.CELL_SIZE / 2) / GomokuBoard.CELL_SIZE;
                    whenMousePressed((byte) r, (byte) c);
                }
            });
        }

        // Auto-play for CC mode
        if (mode.equals("CC")) {
            ccTimer = new Timer(500, e -> {
                if (!board.getGame().endedGame()) {
                    byte current = board.getGame().currentPlayer();
                    IGameKI<M> ai = (current == board.getGame().getPlayer1()) ? ai1 : ai2;
                    IRegularGame<M> before = board.getGame();
                    IGame<M> next = ai.doBestMove(before);

                    // Log AI move
                    if (Debugger.isDebug()) {
                        IRegularGame<M> after = (IRegularGame<M>) next;
                        byte aiPlayer = before.currentPlayer();
                        Pair<Byte, Byte> move = findLastMove(before, after, aiPlayer);
                        if (move != null) Debugger.log(aiPlayer, move.first, move.second);
                    }

                    board.setGame((IRegularGame<M>) next);

                    if (board.getGame().endedGame()) {
                        ccTimer.stop();
                        showResult();
                    }
                }
            });
            ccTimer.start();
        }

        // Keyboard shortcut: Space to instantly finish CC game
        board.setFocusable(true);
        board.requestFocusInWindow();
        board.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (mode.equals("CC") && e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (ccTimer != null && ccTimer.isRunning()) ccTimer.stop();
                    while (!board.getGame().endedGame()) {
                        byte current = board.getGame().currentPlayer();
                        IGameKI<M> ai = (current == board.getGame().getPlayer1()) ? ai1 : ai2;
                        IRegularGame<M> before = board.getGame();
                        IGame<M> next = ai.doBestMove(before);
                        board.setGame((IRegularGame<M>) next);
                    }
                    showResult();
                }
            }
        });

        SwingUtilities.invokeLater(board::requestFocusInWindow);
    }

    protected void whenMousePressed(byte r, byte c) {
        IRegularGame<M> g = board.getGame();
        if (r < 0 || r >= g.getRows() || c < 0 || c >= g.getCols()) return;
        if (g.endedGame()) {
            showResult();
            return;
        }

        if (g.getAtPosition(r, c) == g.getPlayerNone()) {
            // Human move
            if (Debugger.isDebug())
                Debugger.log(g.currentPlayer(), r, c);

            IRegularGame<M> next = g.setAtPosition(r, c);
            board.setGame(next);
            if (mode.equals("PC")) addToHistory(next);

            if (board.getGame().endedGame()) {
                showResult();
                return;
            }

            // PC mode: AI responds
            if (mode.equals("PC")) {
                IRegularGame<M> before = board.getGame();
                IGame<M> nextAI = ai1.doBestMove(before);

                // Log AI move
                if (Debugger.isDebug()) {
                    IRegularGame<M> after = (IRegularGame<M>) nextAI;
                    byte aiPlayer = before.currentPlayer();
                    Pair<Byte, Byte> move = findLastMove(before, after, aiPlayer);
                    if (move != null) Debugger.log(aiPlayer, move.first, move.second);
                }

                board.setGame((IRegularGame<M>) nextAI);
                addToHistory((IRegularGame<M>) nextAI);

                if (board.getGame().endedGame()) {
                    showResult();
                }
            }
        }
    }

    private void undoTwoMoves() {
        if (!mode.equals("PC") || board.getGame().endedGame()) return;
        if (history.size() <= 1) return;
        if (history.size() > 1) history.remove(history.size() - 1);
        if (history.size() > 1) history.remove(history.size() - 1);
        IRegularGame<M> prev = history.get(history.size() - 1);
        // Always human's turn
        if (prev.currentPlayer() != prev.getPlayer1() && history.size() > 1) {
            history.remove(history.size() - 1);
            prev = history.get(history.size() - 1);
        }
        board.setGame(cloneGame(prev));
        undoButton.setEnabled(!board.getGame().endedGame() && history.size() > 1);
    }

    private void addToHistory(IRegularGame<M> game) {
        history.add(cloneGame(game));
        if (history.size() > 40) history.remove(0);
        undoButton.setEnabled(!board.getGame().endedGame() && history.size() > 1);
    }

    @SuppressWarnings("unchecked")
    private IRegularGame<M> cloneGame(IRegularGame<M> game) {
        try {
            return (IRegularGame<M>) ((Cloneable) game).getClass().getMethod("clone").invoke(game);
        } catch (Exception e) {
            throw new RuntimeException("Failed to clone game", e);
        }
    }

    // Helper to find the last move between two states for a player
    private Pair<Byte, Byte> findLastMove(IRegularGame<M> before, IRegularGame<M> after, byte player) {
        for (byte r = 0; r < before.getRows(); r++) {
            for (byte c = 0; c < before.getCols(); c++) {
                if (before.getAtPosition(r, c) != player &&
                        after.getAtPosition(r, c) == player) {
                    return new Pair<>(r, c);
                }
            }
        }
        return null;
    }

    protected void showResult() {
        IRegularGame<M> g = board.getGame();
        byte winner = 0;
        if (g.wins(g.getPlayer1())) {
            blackWins++;
            winner = g.getPlayer1();
        } else if (g.wins(g.getPlayer2())) {
            whiteWins++;
            winner = g.getPlayer2();
        }
        winLabel.setText("Black: " + blackWins + "  Red: " + whiteWins);

        String msg = g.wins(g.getPlayer1()) ? "Black wins!"
                : g.wins(g.getPlayer2()) ? "Red wins!" : "Draw!";

        if (Debugger.isDebug()) {
            StringBuilder stats = new StringBuilder();
            stats.append("\n=== Game Stats ===\n");
            if (winner != 0) {
                stats.append("\nWinner: Player ").append(winner).append("\n");
                int winnerMoves = 0;
                byte[][] boardArr = new byte[g.getRows()][g.getCols()];
                for (int r = 0; r < g.getRows(); r++) {
                    for (int c = 0; c < g.getCols(); c++) {
                        byte val = g.getAtPosition((byte) r, (byte) c);
                        boardArr[r][c] = val;
                        if (val == winner) winnerMoves++;
                    }
                }
                stats.append("Player ").append(winner).append(" made ").append(winnerMoves).append(" moves.\n");

                int[][] directions = {{1,0},{0,1},{1,1},{1,-1}};
                outer:
                for (int[] d : directions) {
                    for (int r = 0; r < g.getRows(); r++) {
                        for (int c = 0; c < g.getCols(); c++) {
                            boolean win = true;
                            for (int i = 0; i < 5; i++) {
                                int nr = r + i * d[0];
                                int nc = c + i * d[1];
                                if (nr < 0 || nr >= g.getRows() || nc < 0 || nc >= g.getCols()
                                        || boardArr[nr][nc] != winner) {
                                    win = false;
                                    break;
                                }
                            }
                            if (win) {
                                stats.append("Winning 5-move sequence: ");
                                for (int i = 0; i < 5; i++) {
                                    int nr = r + i * d[0];
                                    int nc = c + i * d[1];
                                    stats.append("(").append(nr).append(",").append(nc).append(") ");
                                }
                                stats.append("\n");
                                break outer;
                            }
                        }
                    }
                }
            } else {
                stats.append("Game ended in a draw.\n");
            }
            Debugger.logMessage(stats.toString());
            Debugger.close();
        }

        int opt = JOptionPane.showConfirmDialog(this, msg + " Play again?",
                "Game Over", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            board.setGame((IRegularGame<M>) new Gomoku(g.getRows(), g.getCols()));
            if (mode.equals("CC") && ccTimer != null) {
                ccTimer.restart();
            }
            if (mode.equals("PC")) {
                history.clear();
                history.add(cloneGame(board.getGame()));
                undoButton.setEnabled(true);
            }
            if (Debugger.isDebug()) {
                Debugger.printMoveHeader();
            }
        } else {
            // If NOT playing again, disable Undo
            if (mode.equals("PC")) {
                undoButton.setEnabled(false);
            }
        }
    }
}




