package io.ayoub.acdp.ksql;

import io.confluent.ksql.function.udaf.Udaf;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Map;

import static io.ayoub.acdp.ksql.AceSkillUdaf.AceSkillUdafImpl.newAggregateValue;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AceSkillUdafTest {
    private final Udaf<Struct, Map<String, Struct>, List<Struct>> udaf = AceSkillUdaf.createUdaf();

    @Test
    void savesOneSkill() {
        var in = paramStructWithSkill("Heavy Weapons", 44, 10);

        List<Struct> out = aggregateAllParams(List.of(in));

        assertIterableEquals(List.of(
                paramStructWithSkill("Heavy Weapons", 44, 10)
        ), out);
    }

    @Test
    void savesTwoSkills() {
        var in1 = paramStructWithSkill("Heavy Weapons", 44, 10);
        var in2 = paramStructWithSkill("Light Weapons", 45, 5);

        List<Struct> out = aggregateAllParams(List.of(in1, in2));

        assertIterableEquals(List.of(
                paramStructWithSkill("Heavy Weapons", 44, 10),
                paramStructWithSkill("Light Weapons", 45, 5)
        ), out);
    }

    @Test
    void updatesASkill() {
        var ins = List.of(
                paramStructWithSkill("Heavy Weapons", 44, 10),
                paramStructWithSkill("Light Weapons", 45, 5),
                paramStructWithSkill("Heavy Weapons", 44, 5)
        );

        List<Struct> out = aggregateAllParams(ins);

        assertIterableEquals(List.of(
                paramStructWithSkill("Heavy Weapons", 44, 5),
                paramStructWithSkill("Light Weapons", 45, 5)
        ), out);
    }

    @Test
    void sortsSkills() {
        var ins = List.of(
                paramStructWithSkill("Shield", 48, 5),
                paramStructWithSkill("Gearcraft", 42, 5),
                paramStructWithSkill("MeleeDefense", 6, 10),
                paramStructWithSkill("Recklessness", 50, 10)
        );

        List<Struct> out = aggregateAllParams(ins);

        assertIterableEquals(List.of(
                paramStructWithSkill("MeleeDefense", 6, 10),
                paramStructWithSkill("Gearcraft", 42, 5),
                paramStructWithSkill("Shield", 48, 5),
                paramStructWithSkill("Recklessness", 50, 10)
        ), out);
    }

    private static Struct paramStructWithSkill(String skillName, int skillType, long initialLevel) {
        return new Struct(AceSkillUdaf.SKILL_PARAM_SCHEMA)
                .put("PROPERTY_NAME", skillName)
                .put("PROPERTY_TYPE", skillType)
                .put("INITIAL_LEVEL", initialLevel);
    }

    private List<Struct> aggregateAllParams(List<Struct> ins) {
        var intermediate = newAggregateValue();
        for (Struct element : ins) {
            intermediate = udaf.aggregate(element, intermediate);
        }
        List<Struct> out = udaf.map(intermediate);
        return out;
    }
}