package io.ayoub.acdp.ksql;

import io.ayoub.acdp.model.AcePositionWithName;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;
import org.apache.kafka.common.Configurable;

import java.util.Map;

@UdfDescription(
        name = "ace_map_coords",
        author = "Michael Ayoub",
        version = "0.1",
        description = "A function for converting a landblock and position to map coordinates."
)
public class AceMapCoordinatesUdf implements Configurable {
    @Udf(description = "Return the map coordinates string for the landblock and position.")
    public String coordinates(@UdfParameter Long landBlockId, @UdfParameter double x, @UdfParameter double y, @UdfParameter double z) {
        return new AcePositionWithName("", landBlockId, x, y, z).getMapCoordinatesString().orElse("(inside)");
    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
