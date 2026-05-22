package alerts;

import com.alerts.Alert;
import com.alerts.AlertGenerator;
import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlertGeneratorTest {

    private DataStorage storage;
    private AlertGenerator generator;

    @BeforeEach
    void setup() {
        DataStorage.resetForTesting();
        storage = DataStorage.getInstance();
        generator = new AlertGenerator(storage);
    }

    // --- Blood Pressure Critical Threshold Tests ---

    @Test
    void testHighSystolicTriggersCriticalAlert() {
        storage.addPatientData(1, 185.0, "SystolicPressure", System.currentTimeMillis());
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(hasAlert(generator.getTriggeredAlerts(), "Critical Systolic Blood Pressure"));
    }

    @Test
    void testLowSystolicTriggersCriticalAlert() {
        storage.addPatientData(1, 85.0, "SystolicPressure", System.currentTimeMillis());
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(hasAlert(generator.getTriggeredAlerts(), "Critical Systolic Blood Pressure"));
    }

    @Test
    void testHighDiastolicTriggersCriticalAlert() {
        storage.addPatientData(1, 125.0, "DiastolicPressure", System.currentTimeMillis());
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(hasAlert(generator.getTriggeredAlerts(), "Critical Diastolic Blood Pressure"));
    }

    @Test
    void testNormalBloodPressureDoesNotTriggerAlert() {
        storage.addPatientData(1, 120.0, "SystolicPressure", System.currentTimeMillis());
        storage.addPatientData(1, 80.0, "DiastolicPressure", System.currentTimeMillis());
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertFalse(hasAlert(generator.getTriggeredAlerts(), "Critical Systolic Blood Pressure"));
        assertFalse(hasAlert(generator.getTriggeredAlerts(), "Critical Diastolic Blood Pressure"));
    }

    // --- Blood Pressure Trend Tests ---

    @Test
    void testIncreasingTrendTriggersTrendAlert() {
        long now = System.currentTimeMillis();
        storage.addPatientData(1, 100.0, "SystolicPressure", now - 3000);
        storage.addPatientData(1, 115.0, "SystolicPressure", now - 2000);
        storage.addPatientData(1, 130.0, "SystolicPressure", now - 1000);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(hasAlert(generator.getTriggeredAlerts(), "SystolicPressure Increasing Trend"));
    }

    @Test
    void testDecreasingTrendTriggersTrendAlert() {
        long now = System.currentTimeMillis();
        storage.addPatientData(1, 150.0, "SystolicPressure", now - 3000);
        storage.addPatientData(1, 135.0, "SystolicPressure", now - 2000);
        storage.addPatientData(1, 120.0, "SystolicPressure", now - 1000);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(hasAlert(generator.getTriggeredAlerts(), "SystolicPressure Decreasing Trend"));
    }

    @Test
    void testSmallChangesDoNotTriggerTrendAlert() {
        long now = System.currentTimeMillis();
        storage.addPatientData(1, 120.0, "SystolicPressure", now - 3000);
        storage.addPatientData(1, 125.0, "SystolicPressure", now - 2000);
        storage.addPatientData(1, 130.0, "SystolicPressure", now - 1000);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertFalse(hasAlert(generator.getTriggeredAlerts(), "SystolicPressure Increasing Trend"));
    }

    // --- Blood Saturation Tests ---

    @Test
    void testLowSaturationTriggersAlert() {
        storage.addPatientData(1, 90.0, "Saturation", System.currentTimeMillis());
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(hasAlert(generator.getTriggeredAlerts(), "Low Blood Saturation"));
    }

    @Test
    void testNormalSaturationDoesNotTriggerAlert() {
        storage.addPatientData(1, 95.0, "Saturation", System.currentTimeMillis());
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertFalse(hasAlert(generator.getTriggeredAlerts(), "Low Blood Saturation"));
    }

    @Test
    void testRapidSaturationDropTriggersAlert() {
        long now = System.currentTimeMillis();
        storage.addPatientData(1, 98.0, "Saturation", now - 300000); // 5 min ago
        storage.addPatientData(1, 92.0, "Saturation", now);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(hasAlert(generator.getTriggeredAlerts(), "Rapid Blood Saturation Drop"));
    }

    @Test
    void testSmallSaturationDropDoesNotTriggerAlert() {
        long now = System.currentTimeMillis();
        storage.addPatientData(1, 95.0, "Saturation", now - 300000);
        storage.addPatientData(1, 93.0, "Saturation", now);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertFalse(hasAlert(generator.getTriggeredAlerts(), "Rapid Blood Saturation Drop"));
    }

    // --- Hypotensive Hypoxemia Tests ---

    @Test
    void testHypotensiveHypoxemiaAlertWhenBothLow() {
        long now = System.currentTimeMillis();
        storage.addPatientData(1, 85.0, "SystolicPressure", now);
        storage.addPatientData(1, 88.0, "Saturation", now);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(hasAlert(generator.getTriggeredAlerts(), "Hypotensive Hypoxemia"));
    }

    @Test
    void testNoHypotensiveHypoxemiaWhenOnlySystolicLow() {
        long now = System.currentTimeMillis();
        storage.addPatientData(1, 85.0, "SystolicPressure", now);
        storage.addPatientData(1, 95.0, "Saturation", now);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertFalse(hasAlert(generator.getTriggeredAlerts(), "Hypotensive Hypoxemia"));
    }

    @Test
    void testNoHypotensiveHypoxemiaWhenOnlySaturationLow() {
        long now = System.currentTimeMillis();
        storage.addPatientData(1, 120.0, "SystolicPressure", now);
        storage.addPatientData(1, 88.0, "Saturation", now);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertFalse(hasAlert(generator.getTriggeredAlerts(), "Hypotensive Hypoxemia"));
    }

    // --- Triggered Alert Tests ---

    @Test
    void testTriggeredAlertButtonTriggersAlert() {
        storage.addPatientData(1, 1.0, "Alert", System.currentTimeMillis());
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(hasAlert(generator.getTriggeredAlerts(), "Triggered Alert"));
    }

    @Test
    void testResolvedAlertDoesNotTrigger() {
        storage.addPatientData(1, 0.0, "Alert", System.currentTimeMillis());
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertFalse(hasAlert(generator.getTriggeredAlerts(), "Triggered Alert"));
    }

    // --- ECG Tests ---

    @Test
    void testECGAbnormalPeakTriggersAlert() {
        long now = System.currentTimeMillis();
        // Add 10 normal readings
        for (int i = 0; i < 10; i++) {
            storage.addPatientData(1, 0.3, "ECG", now - (10 - i) * 1000);
        }
        // Add a spike that is way above the average
        storage.addPatientData(1, 2.0, "ECG", now);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertTrue(hasAlert(generator.getTriggeredAlerts(), "ECG Abnormality"));
    }

    @Test
    void testNormalECGDoesNotTriggerAlert() {
        long now = System.currentTimeMillis();
        for (int i = 0; i < 11; i++) {
            storage.addPatientData(1, 0.3, "ECG", now - (11 - i) * 1000);
        }
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);

        assertFalse(hasAlert(generator.getTriggeredAlerts(), "ECG Abnormality"));
    }

    // checks if the alert list has one with the right condition
    private boolean hasAlert(List<Alert> alerts, String condition) {
        for (Alert alert : alerts) {
            if (alert.getCondition().equals(condition)) {
                return true;
            }
        }
        return false;
    }
}
