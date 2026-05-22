package com.data_management;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Reads patient data from .txt files in a given folder.
 * The files come from the HealthDataSimulator output.
 * Each line looks like: Patient ID: X, Timestamp: Y, Label: Z, Data: W
 */
public class FileDataReader implements DataReader {

    private String directory;

    /**
     * Creates a FileDataReader that will read from the given directory.
     *
     * @param directory path to the directory containing the data files
     */
    public FileDataReader(String directory) {
        this.directory = directory;
    }

    /**
     * Reads all .txt files in the folder and puts the records into storage.
     *
     * @param dataStorage the storage to write data into
     * @throws IOException if the folder can't be read
     */
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        Files.walk(Paths.get(directory))
                .filter(path -> path.toString().endsWith(".txt"))
                .forEach(path -> {
                    try {
                        readFile(path, dataStorage);
                    } catch (IOException e) {
                        System.err.println("Error reading file " + path + ": " + e.getMessage());
                    }
                });
    }

    private void readFile(Path filePath, DataStorage dataStorage) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                parseLine(line, dataStorage);
            }
        }
    }

    // Parses a single line and stores it in the DataStorage. Skips lines that can't be parsed.
    private void parseLine(String line, DataStorage dataStorage) {
        try {
            String[] parts = line.split(", ");
            if (parts.length < 4) return;

            int patientId = Integer.parseInt(parts[0].replace("Patient ID: ", "").trim());
            long timestamp = Long.parseLong(parts[1].replace("Timestamp: ", "").trim());
            String label = parts[2].replace("Label: ", "").trim();
            String dataStr = parts[3].replace("Data: ", "").trim();

            // Saturation values have a % sign that needs to be removed before parsing
            if (dataStr.endsWith("%")) {
                dataStr = dataStr.substring(0, dataStr.length() - 1);
            }

            // Alert data uses "triggered" and "resolved" instead of numbers
            double value;
            if (dataStr.equals("triggered")) {
                value = 1.0;
            } else if (dataStr.equals("resolved")) {
                value = 0.0;
            } else {
                value = Double.parseDouble(dataStr);
            }

            dataStorage.addPatientData(patientId, value, label, timestamp);
        } catch (Exception e) {
            System.err.println("Could not parse line: " + line);
        }
    }
}
