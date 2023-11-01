package io.ayoub.acdp.ksql;

import io.ayoub.acdp.model.AttributeType;
import io.ayoub.acdp.model.AttributeTypeValue;
import io.confluent.ksql.function.udaf.Udaf;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Map;

import static io.ayoub.acdp.ksql.AceAttributeUdaf.AceAttributeUdafImpl.newAggregateValue;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AceAttributeUdafTest {
    private final Udaf<Struct, Map<String, Struct>, List<Struct>> udaf = AceAttributeUdaf.createUdaf();

    @Test
    void savesOneAttribute() {
        var in = paramStructWithAttribute(AttributeType.STRENGTH, 100L);

        List<Struct> out = aggregateAllParams(List.of(in));

        assertIterableEquals(List.of(
                returnStructWithAttribute(AttributeType.STRENGTH, 100L),
                returnStructWithAttribute(AttributeType.ENDURANCE, 0L),
                returnStructWithAttribute(AttributeType.QUICKNESS, 0L),
                returnStructWithAttribute(AttributeType.COORDINATION, 0L),
                returnStructWithAttribute(AttributeType.FOCUS, 0L),
                returnStructWithAttribute(AttributeType.SELF, 0L)
        ), out);
    }

    @Test
    void worksWithoutUppercase() {
        var in = new Struct(AceAttributeUdaf.ATTRIBUTE_PARAM_SCHEMA)
                .put("PROPERTY_TYPE", AttributeType.STRENGTH.getValue())
                .put("PROPERTY_NAME", "Strength")
                .put("CURRENT_LEVEL", 100L);

        List<Struct> out = aggregateAllParams(List.of(in));

        assertIterableEquals(List.of(
                returnStructWithAttribute(AttributeType.STRENGTH, 100L),
                returnStructWithAttribute(AttributeType.ENDURANCE, 0L),
                returnStructWithAttribute(AttributeType.QUICKNESS, 0L),
                returnStructWithAttribute(AttributeType.COORDINATION, 0L),
                returnStructWithAttribute(AttributeType.FOCUS, 0L),
                returnStructWithAttribute(AttributeType.SELF, 0L)
        ), out);
    }

    @Test
    void savesTwoAttributes() {
        var in1 = paramStructWithAttribute(AttributeType.STRENGTH, 100L);
        var in2 = paramStructWithAttribute(AttributeType.COORDINATION, 100L);

        List<Struct> out = aggregateAllParams(List.of(in1, in2));

        assertIterableEquals(List.of(
                returnStructWithAttribute(AttributeType.STRENGTH, 100L),
                returnStructWithAttribute(AttributeType.ENDURANCE, 0L),
                returnStructWithAttribute(AttributeType.QUICKNESS, 0L),
                returnStructWithAttribute(AttributeType.COORDINATION, 100L),
                returnStructWithAttribute(AttributeType.FOCUS, 0L),
                returnStructWithAttribute(AttributeType.SELF, 0L)
        ), out);
    }

    @Test
    void updatesAnAttribute() {
        var ins = List.of(
                paramStructWithAttribute(AttributeType.STRENGTH, 100L),
                paramStructWithAttribute(AttributeType.ENDURANCE, 10L),
                paramStructWithAttribute(AttributeType.QUICKNESS, 100L),
                paramStructWithAttribute(AttributeType.COORDINATION, 100L),
                paramStructWithAttribute(AttributeType.FOCUS, 10L),
                paramStructWithAttribute(AttributeType.SELF, 10L),
                paramStructWithAttribute(AttributeType.ENDURANCE, 20L)
        );

        List<Struct> out = aggregateAllParams(ins);

        assertIterableEquals(List.of(
                returnStructWithAttribute(AttributeType.STRENGTH, 100L),
                returnStructWithAttribute(AttributeType.ENDURANCE, 20L),
                returnStructWithAttribute(AttributeType.QUICKNESS, 100L),
                returnStructWithAttribute(AttributeType.COORDINATION, 100L),
                returnStructWithAttribute(AttributeType.FOCUS, 10L),
                returnStructWithAttribute(AttributeType.SELF, 10L)
        ), out);
    }

    @Test
    void updatesWithAllFields() {
        var ins = List.of(
                paramStruct(AttributeType.STRENGTH, 100L, 2L, 3L, 100L),
                paramStruct(AttributeType.ENDURANCE, 10L, 2L, 4L, 10L),
                paramStruct(AttributeType.QUICKNESS, 100L, 2L, 5L, 100L),
                paramStruct(AttributeType.COORDINATION, 100L, 2L, 6L, 100L),
                paramStruct(AttributeType.FOCUS, 10L, 2L, 7L, 10L),
                paramStruct(AttributeType.SELF, 10L, 2L, 8L, 10L),
                paramStruct(AttributeType.ENDURANCE, 10L, 10L, 9L, 20L)
        );

        List<Struct> out = aggregateAllParams(ins);

        assertIterableEquals(List.of(
                returnStruct(AttributeType.STRENGTH, 100L, 2L, 3L, 100L),
                returnStruct(AttributeType.ENDURANCE, 10L, 10L, 9L, 20L),
                returnStruct(AttributeType.QUICKNESS, 100L, 2L, 5L, 100L),
                returnStruct(AttributeType.COORDINATION, 100L, 2L, 6L, 100L),
                returnStruct(AttributeType.FOCUS, 10L, 2L, 7L, 10L),
                returnStruct(AttributeType.SELF, 10L, 2L, 8L, 10L)
        ), out);
    }

    private List<Struct> aggregateAllParams(List<Struct> ins) {
        var intermediate = newAggregateValue();
        for (Struct element : ins) {
            intermediate = udaf.aggregate(element, intermediate);
        }
        List<Struct> out = udaf.map(intermediate);
        return out;
    }

    private static Struct paramStruct(AttributeTypeValue attributeTypeValue, long initialLevel, long numTimesIncreased, long xpSpent, long currentLevel) {
        return new Struct(AceAttributeUdaf.ATTRIBUTE_PARAM_SCHEMA)
                .put("PROPERTY_TYPE", attributeTypeValue.getValue())
                .put("PROPERTY_NAME", attributeTypeValue.getLabel())
                .put("INITIAL_LEVEL", initialLevel)
                .put("NUM_TIMES_INCREASED", numTimesIncreased)
                .put("XP_SPENT", xpSpent)
                .put("CURRENT_LEVEL", currentLevel);
    }

    private static Struct returnStruct(AttributeTypeValue attributeTypeValue, long initialLevel, long numTimesIncreased, long xpSpent, long currentLevel) {
        return new Struct(AceAttributeUdaf.RETURN_INNER_SCHEMA)
                .put("PROPERTY_TYPE", attributeTypeValue.getValue())
                .put("PROPERTY_NAME", attributeTypeValue.getLabel())
                .put("INITIAL_LEVEL", initialLevel)
                .put("NUM_TIMES_INCREASED", numTimesIncreased)
                .put("XP_SPENT", xpSpent)
                .put("CURRENT_LEVEL", currentLevel);
    }

    public static Struct paramStructWithAttribute(AttributeTypeValue attributeTypeValue, long value) {
        return new Struct(AceAttributeUdaf.ATTRIBUTE_PARAM_SCHEMA)
                .put("PROPERTY_TYPE", attributeTypeValue.getValue())
                .put("PROPERTY_NAME", attributeTypeValue.getLabel())
                .put("CURRENT_LEVEL", value);
    }

    private static Struct returnStructWithAttribute(AttributeTypeValue attributeTypeValue, long value) {
        return new Struct(AceAttributeUdaf.RETURN_INNER_SCHEMA)
                .put("PROPERTY_TYPE", attributeTypeValue.getValue())
                .put("PROPERTY_NAME", attributeTypeValue.getLabel())
                .put("CURRENT_LEVEL", value);
    }
}
