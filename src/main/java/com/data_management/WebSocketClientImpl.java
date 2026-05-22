package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;

/**
 * Connects to a WebSocket server and listens for patient data in real time.
 * When a message comes in it parses it and saves it to DataStorage.
 * Messages are expected in the format: patientId,timestamp,label,data
 */
public class WebSocketClientImpl extends WebSocketClient implements DataReader {

    private DataStorage dataStorage;

    /**
     * Sets up the client to connect to the given WebSocket address.
     * Doesn't actually connect yet, call readData() for that.
     *
     * @param serverUri the websocket address to connect to
     */
    public WebSocketClientImpl(URI serverUri) {
        super(serverUri);
    }

    /**
     * Saves the storage reference and starts the WebSocket connection.
     * Data will keep coming in from the server until the connection closes.
     *
     * @param dataStorage where to put incoming patient records
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        this.dataStorage = dataStorage;
        connect(); // starts the connection in the background
    }

    // URI is already set in the constructor so we just call connect() here
    @Override
    public void connect(String serverUri) throws Exception {
        connect();
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("connected to websocket server");
    }

    @Override
    public void onMessage(String message) {
        if (dataStorage == null) return;
        try {
            // split into exactly 4 parts: patientId, timestamp, label, data
            String[] parts = message.split(",", 4);
            if (parts.length < 4) {
                System.err.println("message doesn't have enough fields: " + message);
                return;
            }

            int patientId = Integer.parseInt(parts[0].trim());
            long timestamp = Long.parseLong(parts[1].trim());
            String label = parts[2].trim();
            String data = parts[3].trim();

            // saturation values have a % sign we need to remove
            if (data.endsWith("%")) {
                data = data.substring(0, data.length() - 1);
            }

            double value = Double.parseDouble(data);
            dataStorage.addPatientData(patientId, value, label, timestamp);

        } catch (NumberFormatException e) {
            System.err.println("could not parse numbers in message: " + message);
        } catch (Exception e) {
            System.err.println("something went wrong parsing message: " + message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("disconnected from websocket server, reason: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("websocket error: " + ex.getMessage());
    }

    /**
     * Sets the data storage directly, mainly useful for testing without
     * needing an actual websocket connection.
     *
     * @param dataStorage the storage to use
     */
    public void setDataStorage(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }
}
