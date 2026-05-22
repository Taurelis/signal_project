package data_management;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import com.data_management.WebSocketClientImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebSocketClientTest {

    private WebSocketClientImpl client;
    private DataStorage storage;

    @BeforeEach
    void setup() throws Exception {
        DataStorage.resetForTesting();
        storage = DataStorage.getInstance();
        // fake URI - we won't actually connect in these tests
        client = new WebSocketClientImpl(new URI("ws://localhost:8080"));
        client.setDataStorage(storage);
    }

    // --- Normal message parsing ---

    @Test
    void testNormalMessageGetsStoredCorrectly() {
        client.onMessage("1,1000,ECG,0.5");
        List<PatientRecord> records = storage.getRecords(1, 0, Long.MAX_VALUE);
        assertEquals(1, records.size());
        assertEquals(0.5, records.get(0).getMeasurementValue());
        assertEquals("ECG", records.get(0).getRecordType());
    }

    @Test
    void testSaturationMessageStripsPercentSign() {
        client.onMessage("2,2000,Saturation,97%");
        List<PatientRecord> records = storage.getRecords(2, 0, Long.MAX_VALUE);
        assertEquals(1, records.size());
        assertEquals(97.0, records.get(0).getMeasurementValue());
    }

    @Test
    void testBloodPressureMessageStored() {
        client.onMessage("3,3000,SystolicPressure,120.0");
        List<PatientRecord> records = storage.getRecords(3, 0, Long.MAX_VALUE);
        assertEquals(1, records.size());
        assertEquals(120.0, records.get(0).getMeasurementValue());
    }

    @Test
    void testMultipleMessagesForSamePatient() {
        client.onMessage("1,1000,ECG,0.3");
        client.onMessage("1,2000,ECG,0.4");
        List<PatientRecord> records = storage.getRecords(1, 0, Long.MAX_VALUE);
        assertEquals(2, records.size());
    }

    @Test
    void testMessagesForDifferentPatients() {
        client.onMessage("1,1000,ECG,0.3");
        client.onMessage("2,1000,ECG,0.4");
        assertEquals(1, storage.getRecords(1, 0, Long.MAX_VALUE).size());
        assertEquals(1, storage.getRecords(2, 0, Long.MAX_VALUE).size());
    }

    // --- Error handling / bad input ---

    @Test
    void testTooFewFieldsDoesNotCrash() {
        // should just skip the message without throwing
        assertDoesNotThrow(() -> client.onMessage("1,1000,ECG"));
    }

    @Test
    void testEmptyMessageDoesNotCrash() {
        assertDoesNotThrow(() -> client.onMessage(""));
    }

    @Test
    void testNonNumericPatientIdDoesNotCrash() {
        assertDoesNotThrow(() -> client.onMessage("abc,1000,ECG,0.5"));
    }

    @Test
    void testNonNumericValueDoesNotCrash() {
        assertDoesNotThrow(() -> client.onMessage("1,1000,ECG,notanumber"));
    }

    @Test
    void testOnErrorDoesNotCrash() {
        assertDoesNotThrow(() -> client.onError(new Exception("test error")));
    }

    @Test
    void testOnCloseDoesNotCrash() {
        assertDoesNotThrow(() -> client.onClose(1000, "normal close", true));
    }

    @Test
    void testBadMessageDoesNotStoreAnything() {
        client.onMessage("garbage");
        // no records should have been added
        assertEquals(0, storage.getAllPatients().size());
    }
}
