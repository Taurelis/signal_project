package com.alerts;

// alert specifically for blood pressure problems
public class BloodPressureAlert extends Alert {

    public BloodPressureAlert(String patientId, String condition, long timestamp) {
        super(patientId, condition, timestamp);
    }
}
