package io.ayoub.acdp.ksql;

import io.ayoub.acdp.data.pois.PoisDbImporter;
import io.ayoub.acdp.model.AcePositionWithName;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AceNearestPoiUdfTest {
    private final AceNearestPoiUdf udf = new AceNearestPoiUdf();

    @BeforeAll
    void createUdf() {
        udf.configure(Map.of());
    }

    @Test
    void aceNearest() {
        var rithwic = getPositionWithName("Rithwic");

        assertEquals(rithwic.getName(), udf.aceNearest(rithwic.getObjCellId(), rithwic.getOriginX(), rithwic.getOriginY(), rithwic.getOriginZ()));
    }

    @Test
    void aceNearestK() {
        var rithwic = getPositionWithName("Rithwic");

        assertIterableEquals(List.of("Rithwic", "Eastham", "Lytelthorpe", "Cragstone"), udf.aceNearestK(rithwic.getObjCellId(), rithwic.getOriginX(), rithwic.getOriginY(), rithwic.getOriginZ(), 4));
    }

    private static AcePositionWithName getPositionWithName(String name) {
        return new PoisDbImporter().getPois().stream().map(AcePositionWithName::fromPoi).filter(pos -> pos.getName().equals(name)).findFirst().get();
    }
}