package com.alerts.factory;

import com.alerts.Alert;
import com.alerts.ECGAlert;

/**
 * Makes ECG alerts.
 */
public class ECGAlertFactory extends AlertFactory {

    @Override
    public Alert createAlert(String patientId, String condition, long timestamp) {
        return new ECGAlert(patientId, condition, timestamp);
    }
}
