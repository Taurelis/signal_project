package alerts;

import com.cardio_generator.HealthDataSimulator;
import com.data_management.DataStorage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SingletonPatternTest {

    @Test
    void testDataStorageGetInstanceNotNull() {
        DataStorage.resetForTesting();
        assertNotNull(DataStorage.getInstance());
    }

    @Test
    void testDataStorageReturnsSameInstance() {
        DataStorage.resetForTesting();
        DataStorage a = DataStorage.getInstance();
        DataStorage b = DataStorage.getInstance();
        assertSame(a, b);
    }

    @Test
    void testDataStorageDataPersistsBetweenGetInstanceCalls() {
        DataStorage.resetForTesting();
        DataStorage.getInstance().addPatientData(1, 100.0, "HeartRate", 1000L);
        // getting the instance again should still have the data
        assertEquals(1, DataStorage.getInstance().getAllPatients().size());
        DataStorage.resetForTesting();
    }

    @Test
    void testHealthDataSimulatorGetInstanceNotNull() {
        assertNotNull(HealthDataSimulator.getInstance());
    }

    @Test
    void testHealthDataSimulatorReturnsSameInstance() {
        HealthDataSimulator a = HealthDataSimulator.getInstance();
        HealthDataSimulator b = HealthDataSimulator.getInstance();
        assertSame(a, b);
    }
}
