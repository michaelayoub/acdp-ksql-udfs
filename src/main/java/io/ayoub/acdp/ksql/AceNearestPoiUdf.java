package io.ayoub.acdp.ksql;

import io.ayoub.acdp.data.pois.PoisDbImporter;
import io.ayoub.acdp.model.AcePositionWithName;
import io.ayoub.acdp.neighbor.NeighborSearch;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;
import org.apache.kafka.common.Configurable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@UdfDescription(
        name = "ace_nearest_poi",
        author = "Michael Ayoub",
        version = "0.1",
        description = "A function for finding the nearest POIs to a position."
)
public class AceNearestPoiUdf implements Configurable {
    private NeighborSearch neighborSearch;

    @Udf(description = "Return the nearest POI to a position.")
    public String aceNearest(@UdfParameter Long landBlockId, @UdfParameter double x, @UdfParameter double y, @UdfParameter double z) {
        return aceNearestK(landBlockId, x, y, z, 1).get(0);
    }

    @Udf(description = "Return the nearest k POIs to a position.")
    public List<String> aceNearestK(@UdfParameter Long landBlockId, @UdfParameter double x, @UdfParameter double y, @UdfParameter double z, @UdfParameter int k) {
        final var position = new AcePositionWithName("Unknown", landBlockId, x, y, z);
        final var neighbors = neighborSearch.getAlgo().getKNearestNeighbors(position, k);
        return neighbors.stream().map(AcePositionWithName::getName).collect(Collectors.toList());
    }

    @Override
    public void configure(Map<String, ?> configs) {
        neighborSearch = new NeighborSearch(new PoisDbImporter().getPois().stream().map(AcePositionWithName::fromPoi).collect(Collectors.toList()));
    }
}
