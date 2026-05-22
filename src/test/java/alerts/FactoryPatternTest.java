package alerts;

import com.alerts.Alert;
import com.alerts.BloodOxygenAlert;
import com.alerts.BloodPressureAlert;
import com.alerts.ECGAlert;
import com.alerts.factory.BloodOxygenAlertFactory;
import com.alerts.factory.BloodPressureAlertFactory;
import com.alerts.factory.ECGAlertFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FactoryPatternTest {

    @Test
    void testBloodPressureFactoryMakesBloodPressureAlert() {
        BloodPressureAlertFactory factory = new BloodPressureAlertFactory();
        Alert alert = factory.createAlert("1", "Critical Systolic Blood Pressure", 1000L);
        assertTrue(alert instanceof BloodPressureAlert);
    }

    @Test
    void testBloodOxygenFactoryMakesBloodOxygenAlert() {
        BloodOxygenAlertFactory factory = new BloodOxygenAlertFactory();
        Alert alert = factory.createAlert("1", "Low Blood Saturation", 1000L);
        assertTrue(alert instanceof BloodOxygenAlert);
    }

    @Test
    void testECGFactoryMakesECGAlert() {
        ECGAlertFactory factory = new ECGAlertFactory();
        Alert alert = factory.createAlert("1", "ECG Abnormality", 1000L);
        assertTrue(alert instanceof ECGAlert);
    }

    @Test
    void testFactoryAlertHasCorrectPatientId() {
        BloodPressureAlertFactory factory = new BloodPressureAlertFactory();
        Alert alert = factory.createAlert("42", "some condition", 5000L);
        assertEquals("42", alert.getPatientId());
    }

    @Test
    void testFactoryAlertHasCorrectCondition() {
        ECGAlertFactory factory = new ECGAlertFactory();
        Alert alert = factory.createAlert("1", "ECG Abnormality", 1000L);
        assertEquals("ECG Abnormality", alert.getCondition());
    }

    @Test
    void testFactoryAlertHasCorrectTimestamp() {
        BloodOxygenAlertFactory factory = new BloodOxygenAlertFactory();
        Alert alert = factory.createAlert("1", "Low Blood Saturation", 9999L);
        assertEquals(9999L, alert.getTimestamp());
    }
}
