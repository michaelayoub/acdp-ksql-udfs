package io.ayoub.acdp.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AcePositionWithNameTest {

    @Test
    void fromPoi() {
        // TODO
        assertEquals(1, 1);
    }

    @Test
    void getMapCoordinatesString() {
        final var position = new AcePositionWithName("Unknown", 2880634900L, 50.0280151367187, 95.1958694458007, 64.1740036010742);
        final var coordinates = position.getMapCoordinatesString().get();

        assertEquals("41.5N, 35.0E", coordinates);
    }

    @Test
    void getMapCoordinatesInside() {
        final var position = new AcePositionWithName("Unknown", 2880635138L, 84.3781356811523, 106.862091064453, 65.2050018310546);
        final var coordinates = position.getMapCoordinatesString();

        assertTrue(coordinates.isEmpty());
    }
}