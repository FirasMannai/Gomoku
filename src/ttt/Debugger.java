/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package ttt;

import java.io.*;

public class Debugger {
    private static PrintWriter fileWriter;
    private static boolean enabled = false;
    private static boolean headerPrinted = false;

    public static void init(boolean debugMode) throws IOException {
        enabled = debugMode;
        headerPrinted = false;
        if (enabled) {
            fileWriter = new PrintWriter(new FileWriter("gomoku_debug.txt"));
        }
    }

    public static boolean isDebug() {
        return enabled;
    }

    // Print header ONCE at the start of each game
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

    public static void log(int player, int row, int col) {
        if (!enabled) return;
        printMoveHeader(); // Only prints once
        String msg = String.format("P%-6d%-4d%-4d", player,col,row);
        System.out.println(msg);
        if (fileWriter != null) fileWriter.println(msg);
    }
    public static void logMessage(String msg) {
    if (isDebug()) System.out.println(msg);
    if (fileWriter != null) fileWriter.println(msg);
}


    public static void close() {
        if (fileWriter != null) {
            fileWriter.close();
            fileWriter = null;
        }
        headerPrinted = false; // Reset for next game
    }
}
