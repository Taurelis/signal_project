package com.cardio_generator.outputs;

/**
 * Interface that defines how generated patient data should be output.
 * Different implementations can send the data to the console, a file,
 * or over a network connection.
 */
public interface OutputStrategy {

    /**
     * Outputs a single data record for a patient.
     *
     * @param patientId the ID of the patient the data belongs to
     * @param timestamp the time the data was recorded, in milliseconds since epoch
     * @param label     the type of measurement, for example "ECG" or "Saturation"
     * @param data      the actual value of the measurement as a string
     */
    void output(int patientId, long timestamp, String label, String data);
}
