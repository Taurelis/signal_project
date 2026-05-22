package com.alerts.decorators;

import com.alerts.Alert;

/**
 * Wraps an alert and marks it as something that has been triggered multiple times.
 * The repeat count gets added to the condition string so it's visible.
 */
public class RepeatedAlertDecorator extends AlertDecorator {

    private int repeatCount;

    public RepeatedAlertDecorator(Alert alert, int repeatCount) {
        super(alert);
        this.repeatCount = repeatCount;
    }

    @Override
    public String getCondition() {
        return wrappedAlert.getCondition() + " (repeated " + repeatCount + "x)";
    }

    public int getRepeatCount() {
        return repeatCount;
    }
}
