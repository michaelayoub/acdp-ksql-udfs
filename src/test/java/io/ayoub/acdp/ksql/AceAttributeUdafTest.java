package io.ayoub.acdp.ksql;

import io.ayoub.acdp.model.AttributeType;
import io.ayoub.acdp.model.AttributeTypeValue;
import io.confluent.ksql.function.udaf.Udaf;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AceAttributeUdafTest {
    private final Udaf<Struct, Struct, List<Long>> udaf = AceAttributeUdaf.createUdaf();

    @Test
    void savesOneAttribute() {
        var in = structWithAttribute(AttributeType.STRENGTH, 100L);

        var intermediate = udaf.aggregate(in, new Struct(AceAttributeUdaf.AGGREGATE_SCHEMA));
        var out = udaf.map(intermediate);
        assertIterableEquals(List.of(100L, 0L, 0L, 0L, 0L, 0L), out);
    }

    @Test
    void worksWithoutUppercase() {
        var in = new Struct(AceAttributeUdaf.PARAM_SCHEMA)
                .put("ATTRIBUTE_TYPE", AttributeType.STRENGTH.getValue())
                .put("ATTRIBUTE_NAME", "Strength")
                .put("ATTRIBUTE_VALUE", 100L);

        var intermediate = udaf.aggregate(in, new Struct(AceAttributeUdaf.AGGREGATE_SCHEMA));
        var out = udaf.map(intermediate);
        assertIterableEquals(List.of(100L, 0L, 0L, 0L, 0L, 0L), out);
    }

    @Test
    void savesTwoAttributes() {
        var in1 = structWithAttribute(AttributeType.STRENGTH, 100L);
        var in2 = structWithAttribute(AttributeType.COORDINATION, 100L);

        var intermediate = udaf.aggregate(in2, udaf.aggregate(in1, new Struct(AceAttributeUdaf.AGGREGATE_SCHEMA)));
        var out = udaf.map(intermediate);
        assertIterableEquals(out, List.of(100L, 0L, 0L, 100L, 0L, 0L));
    }

    @Test
    void updatesAnAttribute() {
        var ins = List.of(
                structWithAttribute(AttributeType.STRENGTH, 100L),
                structWithAttribute(AttributeType.ENDURANCE, 10L),
                structWithAttribute(AttributeType.QUICKNESS, 100L),
                structWithAttribute(AttributeType.COORDINATION, 100L),
                structWithAttribute(AttributeType.FOCUS, 10L),
                structWithAttribute(AttributeType.SELF, 10L),
                structWithAttribute(AttributeType.ENDURANCE, 20L)
        );

        var intermediate = ins.stream().reduce(new Struct(AceAttributeUdaf.AGGREGATE_SCHEMA), (result, element) -> {
            return udaf.aggregate(element, result);
        });
        var out = udaf.map(intermediate);

        assertIterableEquals(out, List.of(100L, 20L, 100L, 100L, 10L, 10L));
    }

    private static Struct structWithAttribute(AttributeTypeValue typeValue, long value) {
        return new Struct(AceAttributeUdaf.PARAM_SCHEMA)
                .put("ATTRIBUTE_TYPE", typeValue.getValue())
                .put("ATTRIBUTE_NAME", typeValue.getLabel())
                .put("ATTRIBUTE_VALUE", value);
    }
}
