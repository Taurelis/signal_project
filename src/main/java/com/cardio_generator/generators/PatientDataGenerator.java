package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Interface for classes that generate simulated health data for a patient.
 * Each data type (ECG, blood pressure, etc.) has its own implementation.
 * The HealthDataSimulator uses this interface to schedule data generation
 * for all patients without needing to know the specific type of data.
 */
public interface PatientDataGenerator {

    /**
     * Generates one health data reading for the given patient and passes it
     * to the output strategy to be stored or displayed.
     *
     * @param patientId      the unique ID of the patient to generate data for
     * @param outputStrategy the output strategy that will handle the generated data
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
