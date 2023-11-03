package io.ayoub.acdp.ksql;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AceMapCoordinatesUdfTest {
    private final AceMapCoordinatesUdf udf = new AceMapCoordinatesUdf();

    @BeforeAll
    void createUdf() { udf.configure(Map.of()); }

    @Test
    void coordinates() {
        assertEquals("41.5N, 35.0E", udf.coordinates(2880634900L, 50.0280151367187, 95.1958694458007, 64.1740036010742));
    }
}