package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Goes through patient data in DataStorage and triggers alerts when
 * something looks wrong. All triggered alerts are saved in a list
 * so they can be checked later with getTriggeredAlerts().
 */
public class AlertGenerator {

    private DataStorage dataStorage;
    private List<Alert> triggeredAlerts;

    /**
     * Creates an AlertGenerator that reads data from the given storage.
     *
     * @param dataStorage the data storage to read patient records from
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.triggeredAlerts = new ArrayList<>();
    }

    /**
     * Checks all relevant alert conditions for the given patient.
     * Any alerts that are triggered get added to the triggeredAlerts list.
     *
     * @param patient the patient to evaluate
     */
    public void evaluateData(Patient patient) {
        int patientId = patient.getPatientId();
        long now = System.currentTimeMillis();
        long tenMinutesAgo = now - 10 * 60 * 1000;

        List<PatientRecord> allRecords = dataStorage.getRecords(patientId, 0, now);

        List<PatientRecord> systolicRecords = filterByType(allRecords, "SystolicPressure");
        List<PatientRecord> diastolicRecords = filterByType(allRecords, "DiastolicPressure");
        List<PatientRecord> saturationRecords = filterByType(allRecords, "Saturation");
        List<PatientRecord> ecgRecords = filterByType(allRecords, "ECG");
        List<PatientRecord> alertRecords = filterByType(allRecords, "Alert");

        checkBloodPressureTrend(patient, systolicRecords, "SystolicPressure");
        checkBloodPressureTrend(patient, diastolicRecords, "DiastolicPressure");
        checkCriticalBloodPressure(patient, systolicRecords, diastolicRecords);
        checkLowSaturation(patient, saturationRecords);
        checkRapidSaturationDrop(patient, saturationRecords, tenMinutesAgo);
        checkHypotensiveHypoxemia(patient, systolicRecords, saturationRecords);
        checkECGAbnormality(patient, ecgRecords);
        checkTriggeredAlert(patient, alertRecords);
    }

    /**
     * Returns all alerts that were triggered so far.
     *
     * @return list of triggered alerts
     */
    public List<Alert> getTriggeredAlerts() {
        return new ArrayList<>(triggeredAlerts);
    }

    /**
     * Clears the list of triggered alerts.
     */
    public void clearAlerts() {
        triggeredAlerts.clear();
    }

    // helper to get only records of a specific type from the full list
    private List<PatientRecord> filterByType(List<PatientRecord> records, String type) {
        List<PatientRecord> filtered = new ArrayList<>();
        for (PatientRecord record : records) {
            if (record.getRecordType().equals(type)) {
                filtered.add(record);
            }
        }
        return filtered;
    }

    // look at the last 3 readings and see if they keep going up or down by more than 10 each time
    private void checkBloodPressureTrend(Patient patient, List<PatientRecord> records, String type) {
        if (records.size() < 3) return;

        int n = records.size();
        double a = records.get(n - 3).getMeasurementValue();
        double b = records.get(n - 2).getMeasurementValue();
        double c = records.get(n - 1).getMeasurementValue();

        if (b - a > 10 && c - b > 10) {
            triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                    type + " Increasing Trend", System.currentTimeMillis()));
        } else if (a - b > 10 && b - c > 10) {
            triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                    type + " Decreasing Trend", System.currentTimeMillis()));
        }
    }

    // alerts if the latest blood pressure is dangerously high or too low
    private void checkCriticalBloodPressure(Patient patient, List<PatientRecord> systolicRecords,
            List<PatientRecord> diastolicRecords) {
        if (!systolicRecords.isEmpty()) {
            double systolic = systolicRecords.get(systolicRecords.size() - 1).getMeasurementValue();
            if (systolic > 180 || systolic < 90) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                        "Critical Systolic Blood Pressure", System.currentTimeMillis()));
            }
        }
        if (!diastolicRecords.isEmpty()) {
            double diastolic = diastolicRecords.get(diastolicRecords.size() - 1).getMeasurementValue();
            if (diastolic > 120 || diastolic < 60) {
                triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                        "Critical Diastolic Blood Pressure", System.currentTimeMillis()));
            }
        }
    }

    // if the latest saturation reading is below 92% something is wrong
    private void checkLowSaturation(Patient patient, List<PatientRecord> saturationRecords) {
        if (saturationRecords.isEmpty()) return;
        double latest = saturationRecords.get(saturationRecords.size() - 1).getMeasurementValue();
        if (latest < 92) {
            triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                    "Low Blood Saturation", System.currentTimeMillis()));
        }
    }

    // checks if saturation went down by 5% or more in the last 10 minutes
    private void checkRapidSaturationDrop(Patient patient, List<PatientRecord> saturationRecords, long since) {
        List<PatientRecord> recent = new ArrayList<>();
        for (PatientRecord record : saturationRecords) {
            if (record.getTimestamp() >= since) {
                recent.add(record);
            }
        }
        if (recent.size() < 2) return;

        double max = recent.get(0).getMeasurementValue();
        for (PatientRecord record : recent) {
            if (record.getMeasurementValue() > max) {
                max = record.getMeasurementValue();
            }
        }
        double current = recent.get(recent.size() - 1).getMeasurementValue();
        if (max - current >= 5) {
            triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                    "Rapid Blood Saturation Drop", System.currentTimeMillis()));
        }
    }

    // both systolic below 90 and saturation below 92 at the same time is really bad
    private void checkHypotensiveHypoxemia(Patient patient, List<PatientRecord> systolicRecords,
            List<PatientRecord> saturationRecords) {
        if (systolicRecords.isEmpty() || saturationRecords.isEmpty()) return;

        double systolic = systolicRecords.get(systolicRecords.size() - 1).getMeasurementValue();
        double saturation = saturationRecords.get(saturationRecords.size() - 1).getMeasurementValue();

        if (systolic < 90 && saturation < 92) {
            triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                    "Hypotensive Hypoxemia", System.currentTimeMillis()));
        }
    }

    // compares the latest ECG reading to the last 10 and alerts if it's way higher
    private void checkECGAbnormality(Patient patient, List<PatientRecord> ecgRecords) {
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
            triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                    "ECG Abnormality", System.currentTimeMillis()));
        }
    }

    // if the alert button was pressed (stored as 1.0) then trigger an alert
    private void checkTriggeredAlert(Patient patient, List<PatientRecord> alertRecords) {
        if (alertRecords.isEmpty()) return;
        double latest = alertRecords.get(alertRecords.size() - 1).getMeasurementValue();
        if (latest == 1.0) {
            triggerAlert(new Alert(String.valueOf(patient.getPatientId()),
                    "Triggered Alert", System.currentTimeMillis()));
        }
    }

    /**
     * Records an alert and prints it to the console.
     *
     * @param alert the alert to trigger
     */
    private void triggerAlert(Alert alert) {
        triggeredAlerts.add(alert);
        System.out.println("ALERT: Patient " + alert.getPatientId()
                + " - " + alert.getCondition()
                + " at " + alert.getTimestamp());
    }
}
