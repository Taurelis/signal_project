package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Interface for things that generate simulated health data for patients.
 * Each data type like ECG or blood pressure has its own class that implements this.
 * The HealthDataSimulator calls generate() on all of them in a loop.
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
