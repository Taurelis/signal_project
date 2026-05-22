package alerts;

import com.alerts.Alert;
import com.alerts.strategies.BloodPressureStrategy;
import com.alerts.strategies.HeartRateStrategy;
import com.alerts.strategies.OxygenSaturationStrategy;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StrategyPatternTest {

    // --- Blood Pressure Strategy Tests ---

    @Test
    void testBloodPressureStrategyTriggersCriticalAlert() {
        Patient patient = new Patient(1);
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 185.0, "SystolicPressure", System.currentTimeMillis()));
        List<Alert> alerts = new ArrayList<>();

        new BloodPressureStrategy().checkAlert(patient, records, alerts);

        assertTrue(hasAlert(alerts, "Critical Systolic Blood Pressure"));
    }

    @Test
    void testBloodPressureStrategyTriggersIncreasingTrend() {
        long now = System.currentTimeMillis();
        Patient patient = new Patient(1);
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 100.0, "SystolicPressure", now - 3000));
        records.add(new PatientRecord(1, 115.0, "SystolicPressure", now - 2000));
        records.add(new PatientRecord(1, 130.0, "SystolicPressure", now - 1000));
        List<Alert> alerts = new ArrayList<>();

        new BloodPressureStrategy().checkAlert(patient, records, alerts);

        assertTrue(hasAlert(alerts, "SystolicPressure Increasing Trend"));
    }

    @Test
    void testBloodPressureStrategyNoAlertForNormalValues() {
        Patient patient = new Patient(1);
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 120.0, "SystolicPressure", System.currentTimeMillis()));
        List<Alert> alerts = new ArrayList<>();

        new BloodPressureStrategy().checkAlert(patient, records, alerts);

        assertFalse(hasAlert(alerts, "Critical Systolic Blood Pressure"));
    }

    // --- Oxygen Saturation Strategy Tests ---

    @Test
    void testOxygenStrategyTriggersLowSaturationAlert() {
        Patient patient = new Patient(1);
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 90.0, "Saturation", System.currentTimeMillis()));
        List<Alert> alerts = new ArrayList<>();

        new OxygenSaturationStrategy().checkAlert(patient, records, alerts);

        assertTrue(hasAlert(alerts, "Low Blood Saturation"));
    }

    @Test
    void testOxygenStrategyTriggersRapidDrop() {
        long now = System.currentTimeMillis();
        Patient patient = new Patient(1);
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 98.0, "Saturation", now - 300000));
        records.add(new PatientRecord(1, 92.0, "Saturation", now));
        List<Alert> alerts = new ArrayList<>();

        new OxygenSaturationStrategy().checkAlert(patient, records, alerts);

        assertTrue(hasAlert(alerts, "Rapid Blood Saturation Drop"));
    }

    @Test
    void testOxygenStrategyTriggersHypoxemia() {
        long now = System.currentTimeMillis();
        Patient patient = new Patient(1);
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 85.0, "SystolicPressure", now));
        records.add(new PatientRecord(1, 88.0, "Saturation", now));
        List<Alert> alerts = new ArrayList<>();

        new OxygenSaturationStrategy().checkAlert(patient, records, alerts);

        assertTrue(hasAlert(alerts, "Hypotensive Hypoxemia"));
    }

    // --- Heart Rate Strategy Tests ---

    @Test
    void testHeartRateStrategyTriggersECGAlert() {
        long now = System.currentTimeMillis();
        Patient patient = new Patient(1);
        List<PatientRecord> records = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            records.add(new PatientRecord(1, 0.3, "ECG", now - (10 - i) * 1000));
        }
        records.add(new PatientRecord(1, 2.0, "ECG", now));
        List<Alert> alerts = new ArrayList<>();

        new HeartRateStrategy().checkAlert(patient, records, alerts);

        assertTrue(hasAlert(alerts, "ECG Abnormality"));
    }

    @Test
    void testHeartRateStrategyTriggersAlertButton() {
        Patient patient = new Patient(1);
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(1, 1.0, "Alert", System.currentTimeMillis()));
        List<Alert> alerts = new ArrayList<>();

        new HeartRateStrategy().checkAlert(patient, records, alerts);

        assertTrue(hasAlert(alerts, "Triggered Alert"));
    }

    @Test
    void testHeartRateStrategyNoAlertForNormalECG() {
        long now = System.currentTimeMillis();
        Patient patient = new Patient(1);
        List<PatientRecord> records = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            records.add(new PatientRecord(1, 0.3, "ECG", now - (11 - i) * 1000));
        }
        List<Alert> alerts = new ArrayList<>();

        new HeartRateStrategy().checkAlert(patient, records, alerts);

        assertFalse(hasAlert(alerts, "ECG Abnormality"));
    }

    private boolean hasAlert(List<Alert> alerts, String condition) {
        for (Alert a : alerts) {
            if (a.getCondition().equals(condition)) return true;
        }
        return false;
    }
}
