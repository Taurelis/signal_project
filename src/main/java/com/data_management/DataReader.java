package com.data_management;

import java.io.IOException;

public interface DataReader {

    /**
     * Reads data from a source and puts it into storage.
     * For file readers this just reads the file.
     * For WebSocket readers this starts the connection and keeps receiving data.
     *
     * @param dataStorage where to store the data
     * @throws IOException if something goes wrong reading the data
     */
    void readData(DataStorage dataStorage) throws IOException;

    /**
     * Connects to a server at the given address.
     * File-based readers don't need this so there's a default empty version.
     * WebSocket readers should override this to actually connect.
     *
     * @param serverUri the address to connect to, like ws://localhost:8080
     * @throws Exception if the connection fails
     */
    default void connect(String serverUri) throws Exception {
        // file readers don't need a connection so this does nothing by default
    }
}
