/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ttt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * GomokuBoard is the graphical Swing component that renders the Gomoku board and stones.
 * <p>
 * Draws the board grid, star points, player stones, ghost preview for next move,
 * and highlights the winning sequence with star icons. Interacts with the IRegularGame
 * game state to display the current board and respond to mouse movement.
 *
 * @param <M> The move type for the game.
 */
public class GomokuBoard<M> extends JPanel {
    /**
     * Size of each grid cell in pixels.
     */
    protected static final int CELL_SIZE = 35;

    /**
     * Margin around the board in pixels.
     */
    protected static final int MARGIN = 20;

    /**
     * The current game state for rendering.
     */
    private IRegularGame<M> game;

    /**
     * Row and column for preview (ghost stone) position.
     */
    private int previewRow = -1, previewCol = -1;

    /**
     * Star image used to highlight winning stones.
     */
    private Image starImg;

    /**
     * Constructs a GomokuBoard for the given game state.
     * Sets up preferred size, background, and mouse listeners for preview.
     *
     * @param game The initial game state to render.
     */
    public GomokuBoard(IRegularGame<M> game) {
        this.game = game;
        starImg = new ImageIcon(getClass().getResource("/images/star.png")).getImage();
        setPreferredSize(new Dimension(
            MARGIN * 2 + (game.getCols() - 1) * CELL_SIZE,
            MARGIN * 2 + (game.getRows() - 1) * CELL_SIZE
        ));
        setBackground(new Color(222, 184, 135));

        // Mouse movement: preview ghost stone for next move
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int c = (e.getX() - MARGIN + CELL_SIZE/2) / CELL_SIZE;
                int r = (e.getY() - MARGIN + CELL_SIZE/2) / CELL_SIZE;
                if (r >= 0 && r < game.getRows() && c >= 0 && c < game.getCols()
                        && game.getAtPosition((byte)r, (byte)c) == game.getPlayerNone()) {
                    previewRow = r;
                    previewCol = c;
                } else {
                    previewRow = -1;
                    previewCol = -1;
                }
                repaint();
            }
        });

        // Mouse exit: remove preview
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                previewRow = -1;
                previewCol = -1;
                repaint();
            }
        });
    }

    /**
     * Paints the Gomoku board, stones, preview, and winning highlights.
     * <ul>
     *   <li>Draws the board grid and star points.</li>
     *   <li>Renders stones for both players.</li>
     *   <li>Draws a ghost preview stone for the next move.</li>
     *   <li>Highlights the winning sequence with star icons.</li>
     * </ul>
     *
     * @param g The Graphics context for painting.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw grid lines
        g.setColor(Color.BLACK);
        for (int r = 0; r < game.getRows(); r++) {
            int y = MARGIN + r * CELL_SIZE;
            g.drawLine(MARGIN, y, MARGIN + (game.getCols() - 1) * CELL_SIZE, y);
        }
        for (int c = 0; c < game.getCols(); c++) {
            int x = MARGIN + c * CELL_SIZE;
            g.drawLine(x, MARGIN, x, MARGIN + (game.getRows() - 1) * CELL_SIZE);
        }

        // Draw star points for standard 15x15 board
        if (game.getRows() == 15 && game.getCols() == 15) {
            int[][] stars = {{3,3}, {3,11}, {7,7}, {11,3}, {11,11}};
            g.setColor(Color.BLACK);
            for (int[] s : stars) {
                int xCenter = MARGIN + s[1] * CELL_SIZE;
                int yCenter = MARGIN + s[0] * CELL_SIZE;
                g.fillOval(xCenter - 3, yCenter - 3, 6, 6);
            }
        }

        // Draw stones for both players
        int stoneDiameter = 24;
        int halfStone = stoneDiameter / 2;
        for (int r = 0; r < game.getRows(); r++) {
            for (int c = 0; c < game.getCols(); c++) {
                byte p = game.getAtPosition((byte) r, (byte) c);
                if (p != game.getPlayerNone()) {
                    int xCenter = MARGIN + c * CELL_SIZE;
                    int yCenter = MARGIN + r * CELL_SIZE;
                    int x = xCenter - halfStone;
                    int y = yCenter - halfStone;
                    g.setColor(p == game.getPlayer1() ? Color.BLACK : new Color(255, 0, 0));
                    g.fillOval(x, y, stoneDiameter, stoneDiameter);
                    g.setColor(Color.DARK_GRAY);
                    g.drawOval(x, y, stoneDiameter, stoneDiameter);
                }
            }
        }

        // Draw ghost preview stone for next move
        if (previewRow >= 0 && previewCol >= 0 &&
                game.getAtPosition((byte)previewRow, (byte)previewCol) == game.getPlayerNone()) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            int xCenter = MARGIN + previewCol * CELL_SIZE;
            int yCenter = MARGIN + previewRow * CELL_SIZE;
            int x = xCenter - halfStone;
            int y = yCenter - halfStone;
            g2.setColor(game.currentPlayer() == game.getPlayer1() ? Color.BLACK : new Color(255, 0, 0));
            g2.fillOval(x, y, stoneDiameter, stoneDiameter);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        // Highlight winning sequence with star icons
        if (game instanceof Gomoku) {
            List<Pair<Byte, Byte>> winning = ((Gomoku)game).getWinningStones();
            if (winning != null && winning.size() == 5) {
                int starSize = 26;
                int halfStar = starSize / 2;
                for (Pair<Byte, Byte> win : winning) {
                    int xCenter = MARGIN + win.second * CELL_SIZE;
                    int yCenter = MARGIN + win.first * CELL_SIZE;
                    int x = xCenter - halfStar;
                    int y = yCenter - halfStar;
                    g.drawImage(starImg, x, y, starSize, starSize, this);
                }
            }
        }
    }

    /**
     * Sets the game state to render and repaints the board.
     *
     * @param game The new game state.
     */
    public void setGame(IRegularGame<M> game) {
        this.game = game;
        repaint();
    }

    /**
     * Returns the current game state being rendered.
     *
     * @return The current IRegularGame instance.
     */
    public IRegularGame<M> getGame() {
        return game;
    }
}
