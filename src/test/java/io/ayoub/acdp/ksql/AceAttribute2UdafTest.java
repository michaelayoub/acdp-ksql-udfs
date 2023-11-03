package io.ayoub.acdp.ksql;

import io.confluent.ksql.function.udaf.Udaf;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AceAttribute2UdafTest {
    private final Udaf<Struct, Map<String, Struct>, List<Struct>> udaf = AceAttribute2Udaf.createUdaf();

    @Test
    void savesOneAttribute() {
        var in = paramStructWithAttribute2("MaxHealth", 1, 100);

        List<Struct> out = aggregateAllParams(List.of(in));

        assertIterableEquals(List.of(
                paramStructWithAttribute2("MaxHealth", 1, 100)
        ), out);
    }

    @Test
    void savesTwoAttributes() {
        var in1 = paramStructWithAttribute2("MaxHealth", 1, 100);
        var in2 = paramStructWithAttribute2("MaxStamina", 2, 150);

        List<Struct> out = aggregateAllParams(List.of(in1, in2));

        assertIterableEquals(List.of(
                paramStructWithAttribute2("MaxHealth", 1, 100),
                paramStructWithAttribute2("MaxStamina", 2, 150)
        ), out);
    }

    @Test
    void updatesAnAttribute() {
        var ins = List.of(
                paramStructWithAttribute2("MaxHealth", 1, 100),
                paramStructWithAttribute2("MaxStamina", 2, 150),
                paramStructWithAttribute2("MaxHealth", 1, 125)
        );

        List<Struct> out = aggregateAllParams(ins);

        assertIterableEquals(List.of(
                paramStructWithAttribute2("MaxHealth", 1, 125),
                paramStructWithAttribute2("MaxStamina", 2, 150)
        ), out);
    }

    @Test
    void sortsAttributes() {
        var ins = List.of(
                paramStructWithAttribute2("MaxMana", 3, 100),
                paramStructWithAttribute2("MaxStamina", 2, 150),
                paramStructWithAttribute2("MaxHealth", 1, 125)
        );

        List<Struct> out = aggregateAllParams(ins);

        assertIterableEquals(List.of(
                paramStructWithAttribute2("MaxHealth", 1, 125),
                paramStructWithAttribute2("MaxStamina", 2, 150),
                paramStructWithAttribute2("MaxMana", 3, 100)
        ), out);
    }

    private static Struct paramStructWithAttribute2(String attributeName, int attributeType, long currentLevel) {
        return new Struct(AceAttribute2Udaf.ATTRIBUTE_2_PARAM_SCHEMA)
                .put("PROPERTY_TYPE", attributeType)
                .put("PROPERTY_NAME", attributeName)
                .put("CURRENT_LEVEL", currentLevel);
    }

    private List<Struct> aggregateAllParams(List<Struct> ins) {
        var intermediate = AceSkillUdaf.AceSkillUdafImpl.newAggregateValue();
        for (Struct element : ins) {
            intermediate = udaf.aggregate(element, intermediate);
        }
        List<Struct> out = udaf.map(intermediate);
        return out;
    }
}
