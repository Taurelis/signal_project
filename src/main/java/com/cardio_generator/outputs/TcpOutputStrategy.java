package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * An output strategy that sends patient data to a TCP client.
 * When created, it opens a server socket on the specified port and waits for
 * a client to connect in a background thread. Once a client connects, data is
 * sent to it as comma-separated lines. Only one client connection is supported at a time.
 * If no client is connected yet, output calls are ignored.
 *
 * Data format: patientId,timestamp,label,data
 */
public class TcpOutputStrategy implements OutputStrategy {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;

    /**
     * Creates a TCP server on the given port and starts waiting for a client to connect.
     * The waiting happens in a separate thread so the main program is not blocked.
     *
     * @param port the port number to listen on for incoming connections
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a patient data record to the connected TCP client.
     * If no client is connected yet, this method does nothing.
     *
     * @param patientId the ID of the patient
     * @param timestamp the time of the measurement in milliseconds since epoch
     * @param label     the type of measurement
     * @param data      the measurement value as a string
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
