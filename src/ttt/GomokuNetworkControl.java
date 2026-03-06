///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */


package ttt;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * GomokuNetworkControl manages networked Gomoku gameplay between two players (server/client).
 * <p>
 * Handles game board display, mouse input, move synchronization over the network,
 * and turn management. Uses a background thread to listen for opponent moves and
 * updates the Swing UI safely using SwingUtilities.invokeLater.
 * <p>
 * Server acts as Player 1 (Black), client as Player 2 (Red). Moves are sent and received
 * to keep both boards synchronized. Mouse input is only accepted on the player's turn.
 *
 * @param <M> The move type for the game.
 */
public class GomokuNetworkControl<M> extends JPanel {
    /**
     * The visual game board component for Gomoku.
     */
    protected GomokuBoard<M> board;

    /**
     * Network handler for sending and receiving moves.
     */
    private final GomokuNetwork net;

    /**
     * True if this instance is the server (Player 1), false if client (Player 2).
     */
    private final boolean isServer;

    /**
     * Indicates if it is currently this player's turn.
     */
    private volatile boolean myTurn;

    /**
     * Constructs a GomokuNetworkControl for networked gameplay.
     * <p>
     * Initializes the board, sets up network connection as server or client,
     * and attaches mouse input and a background thread for move synchronization.
     *
     * @param game The initial game state.
     * @param hostOrServer "server" to host, or hostname/IP to connect as client.
     * @param port The network port to use.
     * @throws Exception If network setup fails.
     */
    public GomokuNetworkControl(IRegularGame<M> game, String hostOrServer, int port) throws Exception {
        this.board = new GomokuBoard<>(game);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(board);

        if ("server".equalsIgnoreCase(hostOrServer)) {
            net = new GomokuNetwork(port);
            isServer = true;
            myTurn = true; // Server (Black/P1) starts
        } else {
            net = new GomokuNetwork(hostOrServer, port);
            isServer = false;
            myTurn = false; // Client (Red/P2) waits
        }

        /**
         * Mouse input handler: allows player to make a move only if it's their turn and the game isn't over.
         * Validates click position, updates board, sends move to peer, and checks game result.
         */
        board.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent ev) {
                if (!myTurn || board.getGame().endedGame()) return;
                int c = (ev.getX() - GomokuBoard.MARGIN + GomokuBoard.CELL_SIZE / 2) / GomokuBoard.CELL_SIZE;
                int r = (ev.getY() - GomokuBoard.MARGIN + GomokuBoard.CELL_SIZE / 2) / GomokuBoard.CELL_SIZE;
                IRegularGame<M> g = board.getGame();
                if (r < 0 || r >= g.getRows() || c < 0 || c >= g.getCols()) return;
                if (g.getAtPosition((byte) r, (byte) c) == g.getPlayerNone()) {
                    board.setGame(g.setAtPosition((byte) r, (byte) c));
                    net.sendMove(r, c); // send move to peer
                    myTurn = false;
                    checkResult();
                }
            }
        });

        /**
         * Background thread: listens for opponent's moves from the network.
         * On receiving a move, updates the board and turn using SwingUtilities.invokeLater
         * to ensure thread-safe UI updates.
         */
        new Thread(() -> {
            try {
                while (true) {
                    int[] move = net.receiveMove();
                    if (move == null) break; // connection closed
                    SwingUtilities.invokeLater(() -> {
                        IRegularGame<M> g = board.getGame();
                        if (!g.endedGame()) {
                            board.setGame(g.setAtPosition((byte) move[0], (byte) move[1]));
                            myTurn = true;
                            checkResult();
                        }
                    });
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Connection lost!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }
    

    /**
     * Checks if the game has ended and displays the result.
     * Closes the network connection after game completion.
     */
    private void checkResult() {
        IRegularGame<M> g = board.getGame();
        if (g.endedGame()) {
            String msg = g.wins(g.getPlayer1()) ? "Black wins!"
                    : g.wins(g.getPlayer2()) ? "Red wins!" : "Draw!";
            JOptionPane.showMessageDialog(this, msg, "Game Over", JOptionPane.INFORMATION_MESSAGE);
            try { net.close(); } catch (Exception ignored) {}
        }
    }
}
