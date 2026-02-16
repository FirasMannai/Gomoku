/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ttt;

import java.io.*;
import java.net.*;

public class GomokuNetwork {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    public final boolean isServer;

    // As server
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

    // As client
    public GomokuNetwork(String host, int port) throws IOException {
        System.out.println("Connecting to " + host + ":" + port + " ...");
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        isServer = false;
    }

    public void sendMove(int row, int col) {
        out.println(row + "," + col);
    }

    public int[] receiveMove() throws IOException {
        String line = in.readLine();
        String[] parts = line.split(",");
        return new int[] { Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) };
    }

    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}
