package data_management;

import com.data_management.Patient;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PatientTest {

    @Test
    void testGetRecordsWithinRange() {
        Patient patient = new Patient(1);
        patient.addRecord(100.0, "HeartRate", 1000L);
        patient.addRecord(110.0, "HeartRate", 2000L);
        patient.addRecord(120.0, "HeartRate", 3000L);

        List<PatientRecord> records = patient.getRecords(1000L, 2000L);
        assertEquals(2, records.size());
    }

    @Test
    void testGetRecordsOutsideRange() {
        Patient patient = new Patient(1);
        patient.addRecord(100.0, "HeartRate", 1000L);

        List<PatientRecord> records = patient.getRecords(5000L, 6000L);
        assertTrue(records.isEmpty());
    }

    @Test
    void testGetRecordsIncludesBoundaryTimestamps() {
        Patient patient = new Patient(1);
        patient.addRecord(100.0, "HeartRate", 1000L);
        patient.addRecord(110.0, "HeartRate", 3000L);

        List<PatientRecord> records = patient.getRecords(1000L, 3000L);
        assertEquals(2, records.size());
    }

    @Test
    void testGetRecordsWhenNoRecordsExist() {
        Patient patient = new Patient(1);
        List<PatientRecord> records = patient.getRecords(0L, Long.MAX_VALUE);
        assertTrue(records.isEmpty());
    }

    @Test
    void testGetPatientId() {
        Patient patient = new Patient(42);
        assertEquals(42, patient.getPatientId());
    }
}
