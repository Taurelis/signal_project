package com.alerts;

// alert specifically for ECG / heart rate problems
public class ECGAlert extends Alert {

    public ECGAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }
}
