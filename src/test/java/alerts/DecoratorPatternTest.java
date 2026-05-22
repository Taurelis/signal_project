package alerts;

import com.alerts.Alert;
import com.alerts.decorators.PriorityAlertDecorator;
import com.alerts.decorators.RepeatedAlertDecorator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DecoratorPatternTest {

    @Test
    void testRepeatedDecoratorAddsRepeatCountToCondition() {
        Alert base = new Alert("1", "Critical Systolic Blood Pressure", 1000L);
        RepeatedAlertDecorator repeated = new RepeatedAlertDecorator(base, 3);
        assertEquals("Critical Systolic Blood Pressure (repeated 3x)", repeated.getCondition());
    }

    @Test
    void testPriorityDecoratorAddsPriorityTag() {
        Alert base = new Alert("1", "ECG Abnormality", 1000L);
        PriorityAlertDecorator priority = new PriorityAlertDecorator(base, "HIGH");
        assertEquals("[HIGH] ECG Abnormality", priority.getCondition());
    }

    @Test
    void testDecoratorKeepsOriginalPatientId() {
        Alert base = new Alert("99", "Low Blood Saturation", 1000L);
        RepeatedAlertDecorator repeated = new RepeatedAlertDecorator(base, 2);
        assertEquals("99", repeated.getPatientId());
    }

    @Test
    void testDecoratorKeepsOriginalTimestamp() {
        Alert base = new Alert("1", "Low Blood Saturation", 5000L);
        PriorityAlertDecorator priority = new PriorityAlertDecorator(base, "CRITICAL");
        assertEquals(5000L, priority.getTimestamp());
    }

    @Test
    void testRepeatedDecoratorStoresRepeatCount() {
        Alert base = new Alert("1", "some condition", 1000L);
        RepeatedAlertDecorator repeated = new RepeatedAlertDecorator(base, 5);
        assertEquals(5, repeated.getRepeatCount());
    }

    @Test
    void testPriorityDecoratorStoresPriority() {
        Alert base = new Alert("1", "some condition", 1000L);
        PriorityAlertDecorator priority = new PriorityAlertDecorator(base, "LOW");
        assertEquals("LOW", priority.getPriority());
    }

    @Test
    void testStackingBothDecorators() {
        Alert base = new Alert("1", "ECG Abnormality", 1000L);
        RepeatedAlertDecorator repeated = new RepeatedAlertDecorator(base, 2);
        PriorityAlertDecorator priority = new PriorityAlertDecorator(repeated, "HIGH");
        assertEquals("[HIGH] ECG Abnormality (repeated 2x)", priority.getCondition());
    }
}
