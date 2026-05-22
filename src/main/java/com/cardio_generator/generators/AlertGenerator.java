package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Simulates alert button presses for patients.
 * If a patient already has an active alert, there's a 90% chance it clears each cycle.
 * If there's no active alert, there's a small chance a new one gets triggered.
 * It outputs "triggered" or "resolved" depending on what happened.
 */
public class AlertGenerator implements PatientDataGenerator {

    public static final Random randomGenerator = new Random();
    // Changed AlertStates to alertStates - field names should be lowerCamelCase (Google Style 5.2.6)
    private boolean[] alertStates; // false = resolved, true = pressed

    /**
     * Creates an AlertGenerator for the given number of patients.
     * Initializes all alert states to false (no active alerts).
     *
     * @param patientCount the number of patients this generator will handle
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Runs one cycle of alert simulation for a patient.
     * Either clears the current alert or maybe starts a new one.
     *
     * @param patientId      the ID of the patient
     * @param outputStrategy where to send the output
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (randomGenerator.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                // Changed Lambda to lambda - local variable names should be lowerCamelCase (Google Style 5.2.7)
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
                double p = -Math.expm1(-lambda); // Probability of at least one alert in the period
                boolean alertTriggered = randomGenerator.nextDouble() < p;

                if (alertTriggered) {
                    alertStates[patientId] = true;
                    // Output the alert
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
