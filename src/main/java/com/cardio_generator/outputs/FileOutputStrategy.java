package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An output strategy that writes patient data to text files.
 * Each type of measurement (label) gets its own file inside the base directory.
 * If the file already exists, new data is appended to it.
 * The directory is created automatically if it does not exist yet.
 */
// Changed class name from fileOutputStrategy to FileOutputStrategy
// Google Java Style Guide (5.2.2) says class names should be UpperCamelCase
public class FileOutputStrategy implements OutputStrategy {

    // Changed from BaseDirectory to baseDirectory - non-constant fields should be lowerCamelCase (Google Style 5.2.6)
    private String baseDirectory;

    // Changed from file_map to fileMap - underscores not allowed in non-constant field names (Google Style 5.2.6)
    /**
     * Stores the file path for each label so we don't have to compute it every time.
     */
    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Creates a new FileOutputStrategy that saves files in the given directory.
     *
     * @param baseDirectory the directory where output files will be saved
     */
    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Writes a patient data record to the file corresponding to the given label.
     * The record is written in the format:
     * Patient ID: X, Timestamp: Y, Label: Z, Data: W
     * If the directory or file cannot be created, an error is printed and the method returns early.
     *
     * @param patientId the ID of the patient
     * @param timestamp the time the measurement was taken in milliseconds since epoch
     * @param label     the type of measurement, used as the file name (without extension)
     * @param data      the measurement value to write
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            // Create the directory
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        // Changed FilePath to filePath - local variables should be lowerCamelCase (Google Style 5.2.7)
        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        // Write the data to the file
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}
