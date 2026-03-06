/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ttt;

import java.io.*;
import java.net.*;

/**
 * GomokuNetwork manages the client-server communication for networked Gomoku gameplay.
 * <p>
 * Handles socket connections, move transmission, and synchronization between two players.
 * Moves are sent as comma-separated strings ("row,col") and parsed upon receipt.
 * Uses BufferedReader and PrintWriter for efficient text-based communication.
 * Provides safe connection closure.
 */
public class GomokuNetwork {
    /**
     * The underlying socket for network communication.
     */
    private Socket socket;

    /**
     * BufferedReader for receiving moves from the peer.
     */
    private BufferedReader in;

    /**
     * PrintWriter for sending moves to the peer.
     */
    private PrintWriter out;

    /**
     * True if this instance is the server, false if client.
     */
    public final boolean isServer;

    /**
     * Constructs a GomokuNetwork as a server.
     * <p>
     * Waits for a client to connect, then initializes input/output streams.
     *
     * @param port The port to listen on.
     * @throws IOException If network setup fails.
     */
    public GomokuNetwork(int port) throws IOException {
        ServerSocket server = new ServerSocket(port);
        System.out.println("Waiting for client to connect...");
        socket = server.accept();
        System.out.println("Client connected: " + socket.getInetAddress());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        isServer = true;
        server.close();
    }

    /**
     * Constructs a GomokuNetwork as a client.
     * <p>
     * Connects to the server at the specified host and port, then initializes input/output streams.
     *
     * @param host The server hostname or IP address.
     * @param port The server port to connect to.
     * @throws IOException If connection fails.
     */
    public GomokuNetwork(String host, int port) throws IOException {
        System.out.println("Connecting to " + host + ":" + port + " ...");
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        isServer = false;
    }

    /**
     * Sends a move to the peer as a comma-separated string ("row,col").
     *
     * @param row The row index of the move.
     * @param col The column index of the move.
     */
    public void sendMove(int row, int col) {
        out.println(row + "," + col);
    }

    /**
     * Receives a move from the peer, parses the comma-separated string ("row,col") into integers.
     *
     * @return An array containing the row and column indices of the move.
     * @throws IOException If reading from the network fails.
     */
    public int[] receiveMove() throws IOException {
        String line = in.readLine();
        // Parse received move string into row and column
        String[] parts = line.split(",");
        return new int[] { Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) };
    }

    /**
     * Safely closes the network connection and associated streams.
     *
     * @throws IOException If closing the connection fails.
     */
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
