package data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;

import java.util.List;

class DataStorageTest {

    @Test
    void testAddAndGetRecords() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size());
        assertEquals(100.0, records.get(0).getMeasurementValue());
    }

    @Test
    void testGetRecordsForUnknownPatientReturnsEmpty() {
        DataStorage storage = new DataStorage();
        List<PatientRecord> records = storage.getRecords(999, 0, Long.MAX_VALUE);
        assertTrue(records.isEmpty());
    }

    @Test
    void testGetAllPatients() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100.0, "HeartRate", 1000L);
        storage.addPatientData(2, 200.0, "HeartRate", 1000L);

        assertEquals(2, storage.getAllPatients().size());
    }

    @Test
    void testRecordsFilteredByTimeRange() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100.0, "HeartRate", 1000L);
        storage.addPatientData(1, 200.0, "HeartRate", 2000L);
        storage.addPatientData(1, 300.0, "HeartRate", 3000L);

        List<PatientRecord> records = storage.getRecords(1, 1500L, 2500L);
        assertEquals(1, records.size());
        assertEquals(200.0, records.get(0).getMeasurementValue());
    }

    @Test
    void testMultipleRecordTypesForSamePatient() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 120.0, "SystolicPressure", 1000L);
        storage.addPatientData(1, 95.0, "Saturation", 1001L);

        List<PatientRecord> records = storage.getRecords(1, 0, Long.MAX_VALUE);
        assertEquals(2, records.size());
    }
}
