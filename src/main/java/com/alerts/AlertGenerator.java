package com.alerts;

import com.alerts.strategies.AlertStrategy;
import com.alerts.strategies.BloodPressureStrategy;
import com.alerts.strategies.HeartRateStrategy;
import com.alerts.strategies.OxygenSaturationStrategy;
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
    private List<AlertStrategy> strategies;

    /**
     * Creates an AlertGenerator that reads data from the given storage.
     * Sets up the three default checking strategies.
     *
     * @param dataStorage the data storage to read patient records from
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.triggeredAlerts = new ArrayList<>();

        // set up the strategies that do the actual checking
        this.strategies = new ArrayList<>();
        strategies.add(new BloodPressureStrategy());
        strategies.add(new OxygenSaturationStrategy());
        strategies.add(new HeartRateStrategy());
    }

    /**
     * Runs all the alert checks for a patient by going through each strategy.
     *
     * @param patient the patient to check
     */
    public void evaluateData(Patient patient) {
        long now = System.currentTimeMillis();
        List<PatientRecord> allRecords = dataStorage.getRecords(patient.getPatientId(), 0, now);

        // collect new alerts from each strategy then process them
        List<Alert> newAlerts = new ArrayList<>();
        for (AlertStrategy strategy : strategies) {
            strategy.checkAlert(patient, allRecords, newAlerts);
        }
        for (Alert alert : newAlerts) {
            triggerAlert(alert);
        }
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
