package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated blood oxygen saturation (SpO2) data for patients.
 * Each patient starts with a baseline value between 95% and 100%.
 * On every call, the value changes by -1, 0, or +1 to simulate small fluctuations.
 * The value is kept within the range 90-100% to stay medically realistic.
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {
    private static final Random random = new Random();

    /** Keeps track of the last saturation value for each patient. */
    private int[] lastSaturationValues;

    /**
     * Creates the generator and sets up a starting saturation value for each patient.
     *
     * @param patientCount the number of patients to generate data for
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }

    /**
     * Generates a new blood saturation reading for the given patient and sends it to the output.
     * The new value is based on the previous one, slightly adjusted by a random amount.
     * The output label is "Saturation" and the value is formatted as a percentage string (e.g. "97%").
     *
     * @param patientId      the ID of the patient to generate data for
     * @param outputStrategy the output strategy that receives the generated data
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}
