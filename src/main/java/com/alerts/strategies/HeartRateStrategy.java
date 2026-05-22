package com.alerts.strategies;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks ECG readings for weird spikes and also handles the alert button press.
 */
public class HeartRateStrategy implements AlertStrategy {

    @Override
    public void checkAlert(Patient patient, List<PatientRecord> records, List<Alert> alerts) {
        List<PatientRecord> ecg = filterByType(records, "ECG");
        List<PatientRecord> alertRecords = filterByType(records, "Alert");

        checkECGAbnormality(patient, ecg, alerts);
        checkTriggeredAlert(patient, alertRecords, alerts);
    }

    // compares the latest ECG reading to the last 10 and alerts if it's way higher
    private void checkECGAbnormality(Patient patient, List<PatientRecord> ecgRecords, List<Alert> alerts) {
        if (ecgRecords.size() < 2) return;

        int n = ecgRecords.size();
        int windowSize = Math.min(10, n - 1);
        double sum = 0;
        for (int i = n - 1 - windowSize; i < n - 1; i++) {
            sum += Math.abs(ecgRecords.get(i).getMeasurementValue());
        }
        double avg = sum / windowSize;
        double latest = Math.abs(ecgRecords.get(n - 1).getMeasurementValue());

        if (avg > 0 && latest > avg * 2) {
            alerts.add(new Alert(String.valueOf(patient.getPatientId()),
                    "ECG Abnormality", System.currentTimeMillis()));
        }
    }

    // if the alert button was pressed (stored as 1.0) then trigger an alert
    private void checkTriggeredAlert(Patient patient, List<PatientRecord> alertRecords, List<Alert> alerts) {
        if (alertRecords.isEmpty()) return;
        double latest = alertRecords.get(alertRecords.size() - 1).getMeasurementValue();
        if (latest == 1.0) {
            alerts.add(new Alert(String.valueOf(patient.getPatientId()),
                    "Triggered Alert", System.currentTimeMillis()));
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
