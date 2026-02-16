///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */


package ttt;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

public class GomokuNetworkControl<M> extends JPanel {
    protected GomokuBoard<M> board;
    private final GomokuNetwork net;
    private final boolean isServer; // True for server/Player 1, false for client/Player 2
    private volatile boolean myTurn;

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
            myTurn = false; // Client (White/P2) waits
        }

        // Only allow clicking if it’s your turn and the game isn’t over
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
                    net.sendMove(r, c); // send to peer
                    myTurn = false;
                    checkResult();
                }
            }
        });

        // Thread: receive opponent’s move and update board
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
