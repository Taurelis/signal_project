package com.alerts.strategies;

import com.alerts.Alert;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

/**
 * Interface for alert checking strategies.
 * You make one per type of health metric you want to monitor.
 */
public interface AlertStrategy {

    /**
     * Looks at the patient records and adds alerts to the list if something is wrong.
     *
     * @param patient the patient to check
     * @param records all records for that patient
     * @param alerts  list to add any triggered alerts to
     */
    void checkAlert(Patient patient, List<PatientRecord> records, List<Alert> alerts);
}
