package io.ayoub.acdp.ksql;

import io.ayoub.acdp.model.AttributeType;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AceSkillUdfTest {
    private final AceSkillUdf udf = new AceSkillUdf();

    @BeforeAll
    void createUdf() { udf.configure(Map.of()); }

    @Test
    void calculatesWarMagic() {
        final int WAR_MAGIC_TYPE = 34;
        var skillDetails = new Struct(AceSkillUdf.SKILL_PARAM_SCHEMA).put("PROPERTY_TYPE", WAR_MAGIC_TYPE);
        var attributeDetailList = List.of(
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.STRENGTH, 200L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.ENDURANCE, 290L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.QUICKNESS, 200L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.COORDINATION, 200L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.FOCUS, 290L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.SELF, 290L)
        );

        var enrichedSkill = udf.aceSkillFromAttributes(skillDetails, attributeDetailList);
        assertEquals((int)Math.round((290+290)/4.0), enrichedSkill.getInt32("LEVEL_FROM_ATTRIBUTES"));
    }

    @Test
    void singleAttributeSkill() {
        final int RUN_TYPE = 24;
        var skillDetails = new Struct(AceSkillUdf.SKILL_PARAM_SCHEMA).put("PROPERTY_TYPE", RUN_TYPE);
        var attributeDetailList = List.of(
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.STRENGTH, 200L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.ENDURANCE, 290L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.QUICKNESS, 200L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.COORDINATION, 200L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.FOCUS, 290L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.SELF, 290L)
        );

        var enrichedSkill = udf.aceSkillFromAttributes(skillDetails, attributeDetailList);
        assertEquals(200, enrichedSkill.getInt32("LEVEL_FROM_ATTRIBUTES"));
    }

    @Test
    void singleAttributeSkillWithDivisor() {
        final int ARCANE_LORE_TYPE = 14;
        var skillDetails = new Struct(AceSkillUdf.SKILL_PARAM_SCHEMA).put("PROPERTY_TYPE", ARCANE_LORE_TYPE);
        var attributeDetailList = List.of(
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.STRENGTH, 200L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.ENDURANCE, 290L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.QUICKNESS, 200L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.COORDINATION, 200L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.FOCUS, 290L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.SELF, 290L)
        );

        var enrichedSkill = udf.aceSkillFromAttributes(skillDetails, attributeDetailList);
        assertEquals((int)Math.round(290 / 3.0), enrichedSkill.getInt32("LEVEL_FROM_ATTRIBUTES"));
    }

    @Test
    void noFormulaSkillsShowZero() {
        var attributeDetailList = List.of(
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.STRENGTH, 200L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.ENDURANCE, 290L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.QUICKNESS, 200L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.COORDINATION, 200L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.FOCUS, 290L),
                AceAttributeUdafTest.paramStructWithAttribute(AttributeType.SELF, 290L)
        );

        List<Integer> attributeContributions = new ArrayList<>();
        for (int skillId : AceSkillUdf.noFormulaSkills) {
            var skillDetails = new Struct(AceSkillUdf.SKILL_PARAM_SCHEMA).put("PROPERTY_TYPE", skillId);
            attributeContributions.add(udf.aceSkillFromAttributes(skillDetails, attributeDetailList).getInt32("LEVEL_FROM_ATTRIBUTES"));
        }

        assertTrue(attributeContributions.stream().allMatch(integer -> integer == 0));
    }
}
