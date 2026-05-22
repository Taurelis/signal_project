package com.alerts.strategies;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks oxygen saturation for low levels, rapid drops, and the
 * combined low BP + low oxygen situation.
 */
public class OxygenSaturationStrategy implements AlertStrategy {

    @Override
    public void checkAlert(Patient patient, List<PatientRecord> records, List<Alert> alerts) {
        List<PatientRecord> saturation = filterByType(records, "Saturation");
        List<PatientRecord> systolic = filterByType(records, "SystolicPressure");

        checkLowSaturation(patient, saturation, alerts);
        checkRapidDrop(patient, saturation, alerts);
        checkHypotensiveHypoxemia(patient, systolic, saturation, alerts);
    }

    // if the latest saturation reading is below 92% something is wrong
    private void checkLowSaturation(Patient patient, List<PatientRecord> records, List<Alert> alerts) {
        if (records.isEmpty()) return;
        double latest = records.get(records.size() - 1).getMeasurementValue();
        if (latest < 92) {
            alerts.add(new Alert(String.valueOf(patient.getPatientId()),
                    "Low Blood Saturation", System.currentTimeMillis()));
        }
    }

    // checks if saturation went down by 5% or more in the last 10 minutes
    private void checkRapidDrop(Patient patient, List<PatientRecord> records, List<Alert> alerts) {
        long tenMinutesAgo = System.currentTimeMillis() - 10 * 60 * 1000;
        List<PatientRecord> recent = new ArrayList<>();
        for (PatientRecord r : records) {
            if (r.getTimestamp() >= tenMinutesAgo) recent.add(r);
        }
        if (recent.size() < 2) return;

        double max = recent.get(0).getMeasurementValue();
        for (PatientRecord r : recent) {
            if (r.getMeasurementValue() > max) max = r.getMeasurementValue();
        }
        double current = recent.get(recent.size() - 1).getMeasurementValue();
        if (max - current >= 5) {
            alerts.add(new Alert(String.valueOf(patient.getPatientId()),
                    "Rapid Blood Saturation Drop", System.currentTimeMillis()));
        }
    }

    // both systolic below 90 and saturation below 92 at the same time is really bad
    private void checkHypotensiveHypoxemia(Patient patient, List<PatientRecord> systolic,
            List<PatientRecord> saturation, List<Alert> alerts) {
        if (systolic.isEmpty() || saturation.isEmpty()) return;
        double sys = systolic.get(systolic.size() - 1).getMeasurementValue();
        double sat = saturation.get(saturation.size() - 1).getMeasurementValue();
        if (sys < 90 && sat < 92) {
            alerts.add(new Alert(String.valueOf(patient.getPatientId()),
                    "Hypotensive Hypoxemia", System.currentTimeMillis()));
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
