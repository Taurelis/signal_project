package com.alerts.strategies;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks blood pressure records for dangerous trends and out-of-range values.
 */
public class BloodPressureStrategy implements AlertStrategy {

    @Override
    public void checkAlert(Patient patient, List<PatientRecord> records, List<Alert> alerts) {
        List<PatientRecord> systolic = filterByType(records, "SystolicPressure");
        List<PatientRecord> diastolic = filterByType(records, "DiastolicPressure");

        checkTrend(patient, systolic, "SystolicPressure", alerts);
        checkTrend(patient, diastolic, "DiastolicPressure", alerts);
        checkCriticalThreshold(patient, systolic, diastolic, alerts);
    }

    // look at the last 3 readings and see if they keep going up or down by more than 10 each time
    private void checkTrend(Patient patient, List<PatientRecord> records, String type, List<Alert> alerts) {
        if (records.size() < 3) return;

        int n = records.size();
        double a = records.get(n - 3).getMeasurementValue();
        double b = records.get(n - 2).getMeasurementValue();
        double c = records.get(n - 1).getMeasurementValue();

        if (b - a > 10 && c - b > 10) {
            alerts.add(new Alert(String.valueOf(patient.getPatientId()),
                    type + " Increasing Trend", System.currentTimeMillis()));
        } else if (a - b > 10 && b - c > 10) {
            alerts.add(new Alert(String.valueOf(patient.getPatientId()),
                    type + " Decreasing Trend", System.currentTimeMillis()));
        }
    }

    // alerts if the latest blood pressure is dangerously high or too low
    private void checkCriticalThreshold(Patient patient, List<PatientRecord> systolic,
            List<PatientRecord> diastolic, List<Alert> alerts) {
        if (!systolic.isEmpty()) {
            double val = systolic.get(systolic.size() - 1).getMeasurementValue();
            if (val > 180 || val < 90) {
                alerts.add(new Alert(String.valueOf(patient.getPatientId()),
                        "Critical Systolic Blood Pressure", System.currentTimeMillis()));
            }
        }
        if (!diastolic.isEmpty()) {
            double val = diastolic.get(diastolic.size() - 1).getMeasurementValue();
            if (val > 120 || val < 60) {
                alerts.add(new Alert(String.valueOf(patient.getPatientId()),
                        "Critical Diastolic Blood Pressure", System.currentTimeMillis()));
            }
        }
    }

    private List<PatientRecord> filterByType(List<PatientRecord> records, String type) {
        List<PatientRecord> filtered = new ArrayList<>();
        for (PatientRecord r : records) {
            if (r.getRecordType().equals(type)) filtered.add(r);
        }
        return filtered;
    }
}
