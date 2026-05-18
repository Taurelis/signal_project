package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Generates simulated alert events for patients.
 * Each patient can either have an active alert or not. If an alert is already active,
 * there is a 90% chance it gets resolved on each call. If no alert is active, a new one
 * may be triggered based on a probability calculated from a Poisson distribution
 * with a rate of 0.1 per period.
 * Outputs "triggered" when a new alert starts and "resolved" when it ends.
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
     * Generates an alert event for the given patient.
     * If the patient currently has an active alert, there is a 90% chance it resolves this cycle.
     * Otherwise, a new alert may be triggered based on the computed probability.
     *
     * @param patientId      the ID of the patient to check and generate an alert for
     * @param outputStrategy the output strategy used to record the alert event
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
