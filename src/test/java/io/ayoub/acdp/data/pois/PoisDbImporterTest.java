package io.ayoub.acdp.data.pois;

import io.ayoub.acdp.proto.POIsDBOuterClass.POI;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PoisDbImporterTest {
    PoisDbImporter poisDbImporter;

    @BeforeAll
    void createImporter() { poisDbImporter = new PoisDbImporter(); }

    @Test
    void shouldHaveMoreThanOnePoi() {
        assertTrue(poisDbImporter.getPois().size() > 1);
    }

    @Test
    void allPoisAreOutside() {
        final var outsidePois = poisDbImporter.getPois().stream().filter(this::poiIsOutside);
        assertEquals(poisDbImporter.getPois().size(), outsidePois.count());
    }

    private boolean poiIsOutside(POI poi) {
        return (poi.getObjCellId() & 0xFFFF) < 0x100;
    }
}