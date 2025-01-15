package database.config;

import org.junit.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class CassandraConfigReaderTest {

    @Test
    void testExtractAddresses_ValidInput() {
        String configValue = "127.0.0.1:8080,192.168.1.1:9090,example.com:1234";
        List<String> result = CassandraConfigReader.extractAddresses(configValue);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("127.0.0.1", result.get(0));
        assertEquals("192.168.1.1", result.get(1));
        assertEquals("example.com", result.get(2));
    }

    @Test
    void testExtractAddresses_InvalidNodeFormat() {
        String configValue = "127.0.0.1:8080,invalidNode,example.com:1234";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                CassandraConfigReader.extractAddresses(configValue)
        );

        assertEquals("Invalid node format: invalidNode", exception.getMessage());
    }

    @Test
    void testExtractAddresses_EmptyInput() {
        String configValue = "";

        List<String> result = CassandraConfigReader.extractAddresses(configValue);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractAddresses_WhitespaceAndValidNodes() {
        String configValue = "  127.0.0.1:8080  , 192.168.1.1:9090   ";

        List<String> result = CassandraConfigReader.extractAddresses(configValue);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("127.0.0.1", result.get(0));
        assertEquals("192.168.1.1", result.get(1));
    }
}