/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package ttt;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

public class GomokuMain {
    private static final Pattern MODE_PATTERN = Pattern.compile("(?i)PC|PP|CC");
    private static final Pattern STRAT_PATTERN = Pattern.compile("S[1-4]");

    public static void main(String[] args) {
        // --- 1. Help: Show usage if H/--help/-h
        if (args.length == 1 && (
            args[0].equalsIgnoreCase("H") ||
            args[0].equalsIgnoreCase("--help") ||
            args[0].equalsIgnoreCase("-h"))) {
            printHelpAndExit();
        }

        // --- 2. Networked PP Mode ---
        if (args.length >= 2
                && args[0].equalsIgnoreCase("PP")
                && (args[1].equalsIgnoreCase("server") || args[1].equalsIgnoreCase("localhost"))) {

            String hostOrServer = args[1];
            int port = 12345;
            SwingUtilities.invokeLater(() -> {
                try {
                    JFrame frame = new JFrame("Gomoku PP Networked: " + hostOrServer);
                    IRegularGame<Pair<Byte, Byte>> game0 = new Gomoku();
                    GomokuNetworkControl<Pair<Byte, Byte>> control =
                            new GomokuNetworkControl<>(game0, hostOrServer, port);
                    frame.setContentPane(control);
                    frame.pack();
                    frame.setVisible(true);/// after this test
                    ///
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showFatalError("Failed to start network game: " + ex.getMessage());
                }
            });
            return;
        }

        // --- 3. Argument Parsing & Game Initialization ---
        String mode = "PP";
        String strat1 = null, strat2 = null;
        String xmlLoadPath = null;
        boolean debugMode = false;

        for (String arg : args) {
            String up = arg.toUpperCase();
            if (MODE_PATTERN.matcher(up).matches()) {
                mode = up;
            } else if (up.equals("D")) {
                debugMode = true; // D = both console & file output!
            } else if (STRAT_PATTERN.matcher(up).matches()) {
                if (strat1 == null) strat1 = up;
                else strat2 = up;
            } else if (arg.toLowerCase().startsWith("load=")) {
                xmlLoadPath = arg.substring(arg.indexOf('=') + 1);
            } else {
                // Unknown argument
                System.err.println("[ERROR] Unknown parameter: " + arg);
                System.err.println("Type 'java -jar Gomoku.jar H' for help.");
                System.err.println("Usage: java -jar Gomoku.jar [PC|PP|CC] [D] [S1..S4] [S1..S4] [load=path]");
                System.exit(1);
            }
        }

        // --- 4. Validate & Defaults ---
        if (mode.equals("PP") && (strat1 != null || strat2 != null)) {
            System.err.println("Warning: Strategy ignored in PP mode");
            strat1 = strat2 = null;
        }
        if (mode.equals("PC") && strat1 == null) strat1 = "S1";
        if (mode.equals("CC") && strat1 == null) strat1 = "S1";
        if (mode.equals("CC") && strat2 == null) strat2 = "S2";

        // --- 5. Debugger ---
        try {
            Debugger.init(debugMode);
        } catch (IOException e) {
            System.err.println("[ERROR] Debug init failed: " + e.getMessage());
            System.err.println("Type 'java -jar Gomoku.jar H' for help.");
        }

        // --- 6. Load/Create Game State ---
        IRegularGame<Pair<Byte, Byte>> game;
        File saveFile = new File("savedgame.xml");
        if (xmlLoadPath != null) {
            try {
                game = XMLGameStorage.load(new File(xmlLoadPath));
            } catch (Exception e) {
                showFatalError("Load failed: " + e.getMessage());
                game = new Gomoku();
            }
        } else {
            game = new Gomoku();
        }

        // --- 7. Launch GUI (Local or AI Modes) ---
        final String fMode = mode;
        final String fStrat1 = strat1;
        final String fStrat2 = strat2;
        final IRegularGame<Pair<Byte, Byte>> finalGame = game;

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(
                    "Gomoku " + fMode +
                            (fStrat1 != null ? " " + fStrat1 : "") +
                            (fStrat2 != null && fMode.equals("CC") ? " vs " + fStrat2 : "")
            );
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            GomokuControl<Pair<Byte, Byte>> control;
            if (fMode.equals("CC")) {
                control = new GomokuControl<>(finalGame, createStrategy(fStrat1), createStrategy(fStrat2), fMode);
            } else {
                control = new GomokuControl<>(finalGame, createStrategy(fStrat1), fMode);
            }

            // --- Menu Bar (Save/Load) ---
            JMenuBar menuBar = new JMenuBar();
            JMenu fileMenu = new JMenu("File");

            JMenuItem saveItem = new JMenuItem("Save");
            saveItem.addActionListener(e -> {
                try {
                    XMLGameStorage.save(control.board.getGame(), saveFile);
                    JOptionPane.showMessageDialog(frame, "Game saved to savedgame.xml", "Save", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    showError(frame, "Save failed: " + ex.getMessage());
                }
            });

            JMenuItem loadItem = new JMenuItem("Load");
            loadItem.addActionListener(e -> {
                try {
                    IRegularGame<Pair<Byte, Byte>> loaded = XMLGameStorage.load(saveFile);
                    control.board.setGame(loaded);
                    JOptionPane.showMessageDialog(frame, "Game loaded from savedgame.xml", "Load", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    showError(frame, "Load failed: " + ex.getMessage());
                }
            });

            fileMenu.add(saveItem);
            fileMenu.add(loadItem);
            menuBar.add(fileMenu);
            frame.setJMenuBar(menuBar);

            frame.setContentPane(control);
            frame.pack();
            frame.setVisible(true);
        });
    }

    // --- Helper to print Help message ---
    private static void printHelpAndExit() {
        System.out.println("\n============ Gomoku Help ============\n");
        System.out.println("Usage: java -jar Gomoku.jar [MODE] [OPTIONS]");
        System.out.println("\nModes:");
        System.out.println("  PC             Player vs Computer");
        System.out.println("  PP             Player vs Player (local, one window)");
        System.out.println("  PP server      Start a network server (Player 1, waits for a client)");
        System.out.println("  PP localhost   Join as client (Player 2) to localhost");
        System.out.println("  CC             Computer vs Computer (AI vs AI)");
        System.out.println("\nOptions:");
        System.out.println("  D              Debug: log moves to console and to file (gomoku_debug.txt)");
        System.out.println("  S1..S4         Select AI strategy (S1=Random, S2=Block, S3=Minimax, S4=AlphaBeta)");
        System.out.println("  load=path      Load a saved XML game (e.g. load=savedgame.xml)");
        System.out.println("\nExamples:");
        System.out.println("  java -jar Gomoku.jar PC S1 D");
        System.out.println("  java -jar Gomoku.jar CC S2 S4");
        System.out.println("  java -jar Gomoku.jar PP server");
        System.out.println("  java -jar Gomoku.jar PP localhost");
        System.out.println("  java -jar Gomoku.jar PC D load=savedgame.xml");
        System.out.println("\n======================================\n");
        System.exit(0);
    }

    // --- Helper to show a fatal error (console and dialog) and exit ---
    private static void showFatalError(String msg) {
        System.err.println("[ERROR] " + msg);
        System.err.println("Type 'java -jar Gomoku.jar H' for help.");
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }

    // --- Helper to show a GUI error message (does not exit) ---
    private static void showError(JFrame frame, String msg) {
        System.err.println("[ERROR] " + msg);
        JOptionPane.showMessageDialog(frame, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // --- AI Strategy Factory ---
    private static IGameKI<Pair<Byte, Byte>> createStrategy(String code) {
        if (code == null) return new StrategyRandom<>();
        switch (code) {
            case "S2": return new StrategyBlock<>();
            case "S3": return new StrategyMinimax<>();
            case "S4": return new StrategyAlphaBeta<>();
            default:   return new StrategyRandom<>();
        }
    }
}




















