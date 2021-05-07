package net.ssehub.ai_perf.net;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonParseException;

import net.ssehub.ai_perf.LoggingSetup;
import net.ssehub.ai_perf.model.BooleanParameter;
import net.ssehub.ai_perf.model.DoubleParameter;
import net.ssehub.ai_perf.model.IntParameter;
import net.ssehub.ai_perf.model.ParameterValue;

public class NetworkConnectionTest {

    @Test
    public void timeReadCorrectly() {
        MockPeer mockPeer = new MockPeer("{\"time\": 123456, \"stdev\": 500}");
        
        NetworkConnection connection = assertDoesNotThrow(() -> new NetworkConnection("127.0.0.1"));
        
        MeasureResult result = assertDoesNotThrow(() -> connection.sendTask(new MeasureTask()));
        
        mockPeer.waitUntilFinished();
        checkException(mockPeer);
        
        assertEquals(123456L, result.getTime());
    }
    
    @Test
    public void multipleTimesReadCorrectly() {
        MockPeer mockPeer = new MockPeer("{\"time\": 123456, \"stdev\": 500}{\"time\": 634, \"stdev\": 30}");
        
        NetworkConnection connection = assertDoesNotThrow(() -> new NetworkConnection("127.0.0.1"));
        
        MeasureResult result1 = assertDoesNotThrow(() -> connection.sendTask(new MeasureTask()));
        MeasureResult result2 = assertDoesNotThrow(() -> connection.sendTask(new MeasureTask()));
        
        mockPeer.waitUntilFinished();
        checkException(mockPeer);
        
        assertEquals(123456L, result1.getTime());
        assertEquals(634L, result2.getTime());
    }
    
    @Test
    public void readNoJsonThrows() {
        MockPeer mockPeer = new MockPeer("not json\n");
        
        NetworkConnection connection = assertDoesNotThrow(() -> new NetworkConnection("127.0.0.1"));
        
        IOException exc = assertThrows(IOException.class, () -> connection.sendTask(new MeasureTask()));
        
        mockPeer.waitUntilFinished();
        checkException(mockPeer);
        
        assertTrue(exc.getCause() instanceof JsonParseException);
    }
    
    @Test
    public void readJsonNotObjectThrows() {
        MockPeer mockPeer = new MockPeer("[\"list\"]");
        
        NetworkConnection connection = assertDoesNotThrow(() -> new NetworkConnection("127.0.0.1"));
        
        IOException exc = assertThrows(IOException.class, () -> connection.sendTask(new MeasureTask()));
        
        mockPeer.waitUntilFinished();
        checkException(mockPeer);
        
        assertTrue(exc.getCause() instanceof JsonParseException);
    }
    
    @Test
    public void readJsonMissingTimeThrows() {
        MockPeer mockPeer = new MockPeer("{}");
        
        NetworkConnection connection = assertDoesNotThrow(() -> new NetworkConnection("127.0.0.1"));
        
        IOException exc = assertThrows(IOException.class, () -> connection.sendTask(new MeasureTask()));
        
        mockPeer.waitUntilFinished();
        checkException(mockPeer);
        
        assertTrue(exc.getCause() instanceof JsonParseException);
        assertEquals("Field \"time\" missing", exc.getCause().getMessage());
    }
    
    @Test
    public void readJsonTimeNotLongThrows() {
        MockPeer mockPeer = new MockPeer("{\"time\": \"text\"}");
        
        NetworkConnection connection = assertDoesNotThrow(() -> new NetworkConnection("127.0.0.1"));
        
        IOException exc = assertThrows(IOException.class, () -> connection.sendTask(new MeasureTask()));
        
        mockPeer.waitUntilFinished();
        checkException(mockPeer);
        
        assertTrue(exc.getCause() instanceof JsonParseException);
    }
    
    @Test
    public void readJsonTooManyFieldsThrows() {
        MockPeer mockPeer = new MockPeer("{\"time\": 123456, \"something_else\": true}");
        
        NetworkConnection connection = assertDoesNotThrow(() -> new NetworkConnection("127.0.0.1"));
        
        IOException exc = assertThrows(IOException.class, () -> connection.sendTask(new MeasureTask()));
        
        mockPeer.waitUntilFinished();
        checkException(mockPeer);
        
        assertTrue(exc.getCause() instanceof JsonParseException);
        assertEquals("Field \"something_else\" is extra", exc.getCause().getMessage());
    }
    
    @Test
    public void intParamSentCorrectly() {
        MockPeer mockPeer = new MockPeer("{\"time\": 123456, \"stdev\": 500}\n");
        
        NetworkConnection connection = assertDoesNotThrow(() -> new NetworkConnection("127.0.0.1"));
        
        IntParameter ip = new IntParameter("param1", 10, 0, 1000);
        ParameterValue<Integer> iv = new ParameterValue<Integer>(ip, 453);
        
        assertDoesNotThrow(() -> connection.sendTask(new MeasureTask(iv)));
        
        mockPeer.waitUntilFinished();
        checkException(mockPeer);
        
        assertEquals("{\"parameters\":{\"param1\":453}}\n", mockPeer.getRead());
    }
    
    @Test
    public void booleanParamSentCorrectly() {
        MockPeer mockPeer = new MockPeer("{\"time\": 123456, \"stdev\": 500}\n");
        
        NetworkConnection connection = assertDoesNotThrow(() -> new NetworkConnection("127.0.0.1"));
        
        BooleanParameter ip = new BooleanParameter("param1", false);
        ParameterValue<Boolean> iv = new ParameterValue<Boolean>(ip, true);
        
        assertDoesNotThrow(() -> connection.sendTask(new MeasureTask(iv)));
        
        mockPeer.waitUntilFinished();
        checkException(mockPeer);
        
        assertEquals("{\"parameters\":{\"param1\":true}}\n", mockPeer.getRead());
    }
    
    @Test
    public void doubleParamSentCorrectly() {
        MockPeer mockPeer = new MockPeer("{\"time\": 123456, \"stdev\": 500}\n");
        
        NetworkConnection connection = assertDoesNotThrow(() -> new NetworkConnection("127.0.0.1"));
        
        DoubleParameter ip = new DoubleParameter("param1", 0.00, 0, 100, 0.05);
        ParameterValue<Double> iv = new ParameterValue<Double>(ip, 64.45);
        
        assertDoesNotThrow(() -> connection.sendTask(new MeasureTask(iv)));
        
        mockPeer.waitUntilFinished();
        checkException(mockPeer);
        
        assertEquals("{\"parameters\":{\"param1\":64.45}}\n", mockPeer.getRead());
    }
    
    @Test
    public void multipleParamSentCorrectly() {
        MockPeer mockPeer = new MockPeer("{\"time\": 123456, \"stdev\": 500}\n");
        
        NetworkConnection connection = assertDoesNotThrow(() -> new NetworkConnection("127.0.0.1"));
        
        IntParameter ip0 = new IntParameter("param1", 10, 0, 1000);
        ParameterValue<Integer> iv0 = new ParameterValue<Integer>(ip0, 453);
        
        IntParameter ip1 = new IntParameter("paramX", 5000, 5000, 10000);
        ParameterValue<Integer> iv1 = new ParameterValue<Integer>(ip1, 6324);
        
        IntParameter ip2 = new IntParameter("anotherOne", 0, -1000, 0);
        ParameterValue<Integer> iv2 = new ParameterValue<Integer>(ip2, -867);
        
        assertDoesNotThrow(() -> connection.sendTask(new MeasureTask(iv0, iv1, iv2)));
        
        mockPeer.waitUntilFinished();
        checkException(mockPeer);
        
        assertEquals("{\"parameters\":{\"param1\":453,\"paramX\":6324,\"anotherOne\":-867}}\n", mockPeer.getRead());
    }
    
    private static void checkException(MockPeer mockPeer) {
        assertDoesNotThrow(() -> {
            if (mockPeer.getException() != null) {
                throw mockPeer.getException();
            }
        });
    }
    
    @BeforeAll
    public static void initLogger() {
        LoggingSetup.setup(true, Optional.empty());
    }
    
}
