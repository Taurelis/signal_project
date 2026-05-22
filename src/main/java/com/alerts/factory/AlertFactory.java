package com.alerts.factory;

import com.alerts.Alert;

/**
 * Base class for making alerts.
 * Subclasses decide what kind of alert to actually create.
 */
public abstract class AlertFactory {

    /**
     * Makes a new alert with the given details.
     *
     * @param patientId the patient this alert is for
     * @param condition what went wrong
     * @param timestamp when it happened
     * @return the created alert
     */
    public abstract Alert createAlert(String patientId, String condition, long timestamp);
}
