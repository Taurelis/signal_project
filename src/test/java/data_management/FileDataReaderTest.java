package data_management;

import com.data_management.DataStorage;
import com.data_management.FileDataReader;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileDataReaderTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setup() {
        DataStorage.resetForTesting();
    }

    @Test
    void testReadNormalRecord() throws IOException {
        Path file = tempDir.resolve("SystolicPressure.txt");
        Files.writeString(file,
                "Patient ID: 1, Timestamp: 1000, Label: SystolicPressure, Data: 120.0\n" +
                "Patient ID: 1, Timestamp: 2000, Label: SystolicPressure, Data: 130.0\n");

        DataStorage storage = DataStorage.getInstance();
        new FileDataReader(tempDir.toString()).readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0, Long.MAX_VALUE);
        assertEquals(2, records.size());
        assertEquals(120.0, records.get(0).getMeasurementValue());
        assertEquals(130.0, records.get(1).getMeasurementValue());
    }

    @Test
    void testReadSaturationStripsPercentSign() throws IOException {
        Path file = tempDir.resolve("Saturation.txt");
        Files.writeString(file,
                "Patient ID: 2, Timestamp: 1000, Label: Saturation, Data: 97%\n");

        DataStorage storage = DataStorage.getInstance();
        new FileDataReader(tempDir.toString()).readData(storage);

        List<PatientRecord> records = storage.getRecords(2, 0, Long.MAX_VALUE);
        assertEquals(1, records.size());
        assertEquals(97.0, records.get(0).getMeasurementValue());
    }

    @Test
    void testReadAlertTriggeredAndResolved() throws IOException {
        Path file = tempDir.resolve("Alert.txt");
        Files.writeString(file,
                "Patient ID: 3, Timestamp: 1000, Label: Alert, Data: triggered\n" +
                "Patient ID: 3, Timestamp: 2000, Label: Alert, Data: resolved\n");

        DataStorage storage = DataStorage.getInstance();
        new FileDataReader(tempDir.toString()).readData(storage);

        List<PatientRecord> records = storage.getRecords(3, 0, Long.MAX_VALUE);
        assertEquals(2, records.size());
        assertEquals(1.0, records.get(0).getMeasurementValue());
        assertEquals(0.0, records.get(1).getMeasurementValue());
    }

    @Test
    void testMalformedLineIsSkipped() throws IOException {
        Path file = tempDir.resolve("test.txt");
        Files.writeString(file,
                "this is not valid\n" +
                "Patient ID: 1, Timestamp: 1000, Label: ECG, Data: 0.5\n");

        DataStorage storage = DataStorage.getInstance();
        new FileDataReader(tempDir.toString()).readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0, Long.MAX_VALUE);
        assertEquals(1, records.size());
    }

    @Test
    void testMultiplePatientsInOneFile() throws IOException {
        Path file = tempDir.resolve("ECG.txt");
        Files.writeString(file,
                "Patient ID: 1, Timestamp: 1000, Label: ECG, Data: 0.5\n" +
                "Patient ID: 2, Timestamp: 1000, Label: ECG, Data: 0.6\n");

        DataStorage storage = DataStorage.getInstance();
        new FileDataReader(tempDir.toString()).readData(storage);

        assertEquals(1, storage.getRecords(1, 0, Long.MAX_VALUE).size());
        assertEquals(1, storage.getRecords(2, 0, Long.MAX_VALUE).size());
    }
}
