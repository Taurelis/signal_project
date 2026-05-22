package com.alerts.decorators;

import com.alerts.Alert;

/**
 * Wraps an existing alert so subclasses can add extra info to it
 * without changing the original Alert class.
 */
public abstract class AlertDecorator extends Alert {

    protected Alert wrappedAlert;

    public AlertDecorator(Alert alert) {
        super(alert.getPatientId(), alert.getCondition(), alert.getTimestamp());
        this.wrappedAlert = alert;
    }

    @Override
    public String getCondition() {
        return wrappedAlert.getCondition();
    }
}
