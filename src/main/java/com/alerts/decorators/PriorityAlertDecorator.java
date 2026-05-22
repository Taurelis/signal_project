package com.alerts.decorators;

import com.alerts.Alert;

/**
 * Wraps an alert and adds a priority tag to the front of the condition.
 * So for example a high priority alert would show as "[HIGH] Critical Systolic Blood Pressure".
 */
public class PriorityAlertDecorator extends AlertDecorator {

    private String priority;

    public PriorityAlertDecorator(Alert alert, String priority) {
        super(alert);
        this.priority = priority;
    }

    @Override
    public String getCondition() {
        return "[" + priority + "] " + wrappedAlert.getCondition();
    }

    public String getPriority() {
        return priority;
    }
}
