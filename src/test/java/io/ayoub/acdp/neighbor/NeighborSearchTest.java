package io.ayoub.acdp.neighbor;

import io.ayoub.acdp.data.pois.PoisDbImporter;
import io.ayoub.acdp.model.AcePositionWithName;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class NeighborSearchTest {
    static NeighborSearch neighborSearch;

    @BeforeAll
    static void setNeighborSearch() {
        neighborSearch = new NeighborSearch(new PoisDbImporter().getPois().stream().map(AcePositionWithName::fromPoi).collect(Collectors.toList()));
    }

    @Test
    void nearestRithwic() {
        var rithwic = getPositionWithName("Rithwic");

        var neighbors = neighborSearch.getAlgo().getKNearestNeighbors(rithwic, 4);

        assertIterableEquals(List.of(
                rithwic,
                getPositionWithName("Eastham"),
                getPositionWithName("Lytelthorpe"),
                getPositionWithName("Cragstone")
        ), neighbors);
    }

    private static AcePositionWithName getPositionWithName(String name) {
        return new PoisDbImporter().getPois().stream().map(AcePositionWithName::fromPoi).filter(pos -> pos.getName().equals(name)).findFirst().get();
    }
}