/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package ttt;

import java.io.*;

/**
 * Debugger is a debugging and logging utility for the Gomoku application.
 * <p>
 * Provides static methods for global access to log moves and messages to the console
 * and optionally to a file (gomoku_debug.txt) when debug mode is enabled. Prints a move table header
 * once per game, manages log file creation and closure, and supports debug mode initialization.
 */
public class Debugger {

    /**
     * Writer for the debug log file (gomoku_debug.txt).
     */
    private static PrintWriter fileWriter;

    /**
     * True if debug mode is enabled.
     */
    private static boolean enabled = false;

    /**
     * True if the move table header has been printed for the current game.
     */
    private static boolean headerPrinted = false;

    /**
     * Initializes debug mode and log file.
     *
     * @param debugMode True to enable debug logging.
     * @throws IOException If log file creation fails.
     */
    public static void init(boolean debugMode) throws IOException {
        enabled = debugMode;
        headerPrinted = false;
        if (enabled) {
            fileWriter = new PrintWriter(new FileWriter("gomoku_debug.txt"));
        }
    }

    /**
     * Returns whether debug mode is enabled.
     *
     * @return True if debug mode is enabled.
     */
    public static boolean isDebug() {
        return enabled;
    }

    /**
     * Prints the move table header once per game to console and log file.
     */
    public static void printMoveHeader() {
        if (!enabled) return;
        if (!headerPrinted) {
            String header = String.format("\n%-7s%-4s%-4s", "Player", "x", "y");
            String dash = "--------------------";
            System.out.println(header);
            System.out.println(dash);
            if (fileWriter != null) {
                fileWriter.println(header);
                fileWriter.println(dash);
            }
            headerPrinted = true;
        }
    }

    /**
     * Logs a move to the console and log file, printing the header if needed.
     *
     * @param player The player making the move.
     * @param row The row index of the move.
     * @param col The column index of the move.
     */
    public static void log(int player, int row, int col) {
        if (!enabled) return;
        printMoveHeader(); // Only prints once
        String msg = String.format("P%-6d%-4d%-4d", player,col,row);
        System.out.println(msg);
        if (fileWriter != null) fileWriter.println(msg);
    }

    /**
     * Logs a message to the console and log file.
     *
     * @param msg The message to log.
     */
    public static void logMessage(String msg) {
        if (isDebug()) System.out.println(msg);
        if (fileWriter != null) fileWriter.println(msg);
    }

    /**
     * Closes the log file and resets header state for the next game.
     */
    public static void close() {
        if (fileWriter != null) {
            fileWriter.close();
            fileWriter = null;
        }
        headerPrinted = false;
    }
}
