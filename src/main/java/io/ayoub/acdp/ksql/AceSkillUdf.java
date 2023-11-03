package io.ayoub.acdp.ksql;

import io.ayoub.acdp.data.portalDat.PortalDatDbImporter;
import io.ayoub.acdp.proto.PortalDatDBOuterClass.Skill;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;
import org.apache.kafka.common.Configurable;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import java.util.List;
import java.util.Map;
import java.util.Set;

@UdfDescription(
        name = "ace_skill",
        author = "Michael Ayoub",
        version = "0.1",
        description = "Return the attribute contribution to a skill. `attributes` should be an array of attribute " +
                "values, and `attributeKeys` should be an array with corresponding entries of the attribute types; for " +
                "example, collect_list(current_level) and collect_list(type), respectively."
)
public class AceSkillUdf implements Configurable {
    Map<Integer, Skill> skillMap;

    public static final Set<Integer> noFormulaSkills = Set.of(
            19, // Assess Person
            20, // Deception
            27, // Assess Creature
            35, // Leadership
            36, // Loyalty
            40 // Salvaging
    );

    public static final Schema SKILL_PARAM_SCHEMA = SchemaBuilder
            .struct().optional()
            .field("PROPERTY_TYPE", Schema.OPTIONAL_INT32_SCHEMA)
            .field("PROPERTY_NAME", Schema.OPTIONAL_STRING_SCHEMA)
            .field("INITIAL_LEVEL", Schema.OPTIONAL_INT64_SCHEMA)
            .field("NUM_TIMES_INCREASED", Schema.OPTIONAL_INT32_SCHEMA)
            .field("XP_SPENT", Schema.OPTIONAL_INT64_SCHEMA)
            .field("SKILL_ADVANCEMENT_CLASS_TYPE", Schema.OPTIONAL_INT64_SCHEMA)
            .field("SKILL_ADVANCEMENT_CLASS_NAME", Schema.OPTIONAL_STRING_SCHEMA)
            .field("LAST_USED_TIMESTAMP", Schema.OPTIONAL_STRING_SCHEMA)
            .build();

    public static final String SKILL_PARAM_SCHEMA_DESCRIPTOR =
            "STRUCT<PROPERTY_TYPE INTEGER, " +
                    "PROPERTY_NAME VARCHAR(STRING), " +
                    "INITIAL_LEVEL BIGINT, " +
                    "NUM_TIMES_INCREASED INTEGER, " +
                    "XP_SPENT BIGINT, " +
                    "SKILL_ADVANCEMENT_CLASS_TYPE BIGINT, " +
                    "SKILL_ADVANCEMENT_CLASS_NAME VARCHAR(STRING), " +
                    "LAST_USED_TIMESTAMP VARCHAR(STRING)>";

    public static final String ATTRIBUTE_PARAM_SCHEMA_DESCRIPTOR = AceAttributeUdaf.RETURN_SCHEMA_DESCRIPTOR;

    public static final Schema SKILL_RETURN_SCHEMA = SchemaBuilder
            .struct().optional()
            .field("PROPERTY_TYPE", Schema.OPTIONAL_INT32_SCHEMA)
            .field("PROPERTY_NAME", Schema.OPTIONAL_STRING_SCHEMA)
            .field("INITIAL_LEVEL", Schema.OPTIONAL_INT64_SCHEMA)
            .field("NUM_TIMES_INCREASED", Schema.OPTIONAL_INT32_SCHEMA)
            .field("LEVEL_FROM_ATTRIBUTES", Schema.OPTIONAL_INT32_SCHEMA)
            .field("CURRENT_LEVEL", Schema.OPTIONAL_INT32_SCHEMA)
            .field("XP_SPENT", Schema.OPTIONAL_INT64_SCHEMA)
            .field("SKILL_ADVANCEMENT_CLASS_TYPE", Schema.OPTIONAL_INT64_SCHEMA)
            .field("SKILL_ADVANCEMENT_CLASS_NAME", Schema.OPTIONAL_STRING_SCHEMA)
            .field("LAST_USED_TIMESTAMP", Schema.OPTIONAL_STRING_SCHEMA)
            .build();

    public static final String SKILL_RETURN_SCHEMA_DESCRIPTOR =
            "STRUCT<PROPERTY_TYPE INTEGER, " +
                    "PROPERTY_NAME VARCHAR(STRING), " +
                    "INITIAL_LEVEL BIGINT, " +
                    "NUM_TIMES_INCREASED INTEGER, " +
                    "LEVEL_FROM_ATTRIBUTES INTEGER, " +
                    "CURRENT_LEVEL INTEGER, " +
                    "XP_SPENT BIGINT, " +
                    "SKILL_ADVANCEMENT_CLASS_TYPE BIGINT, " +
                    "SKILL_ADVANCEMENT_CLASS_NAME VARCHAR(STRING), " +
                    "LAST_USED_TIMESTAMP VARCHAR(STRING)>";

    @Udf(schema = SKILL_RETURN_SCHEMA_DESCRIPTOR)
    public Struct aceSkillFromAttributes(
            @UdfParameter(
                    schema = SKILL_PARAM_SCHEMA_DESCRIPTOR
            ) Struct skillDetails,
            @UdfParameter(
                    schema = ATTRIBUTE_PARAM_SCHEMA_DESCRIPTOR
            ) List<Struct> attributeDetailList
    ) {
        int skillId = skillDetails.getInt32("PROPERTY_TYPE");
        if (noFormulaSkills.contains(skillId)) {
            return getEnrichedSkill(skillDetails, 0);
        }

        Skill skill = skillMap.get(skillId);
        long attr1Value = attributeDetailList.get(skill.getAttr1() - 1).getInt64("CURRENT_LEVEL");
        double divisor = skill.getDivisor();

        int levelFromAttributes;
        if (skill.getAttr2() == 0) { // single attribute skill
            levelFromAttributes = (int) Math.round(attr1Value / divisor);
        } else {
            long attr2Value = attributeDetailList.get(skill.getAttr2() - 1).getInt64("CURRENT_LEVEL");
            levelFromAttributes = (int) Math.round((attr1Value + attr2Value) / divisor);
        }

        return getEnrichedSkill(skillDetails, levelFromAttributes);
    }

    private static Struct getEnrichedSkill(Struct skillDetails, int levelFromAttributes) {
        var enrichedSkill = new Struct(SKILL_RETURN_SCHEMA);
        var numTimesIncreased = skillDetails.getInt32("NUM_TIMES_INCREASED");
        var initialLevel = skillDetails.getInt64("INITIAL_LEVEL").intValue();

        enrichedSkill.put("PROPERTY_TYPE", skillDetails.getInt32("PROPERTY_TYPE"));
        enrichedSkill.put("PROPERTY_NAME", skillDetails.getString("PROPERTY_NAME"));
        enrichedSkill.put("INITIAL_LEVEL", skillDetails.getInt64("INITIAL_LEVEL"));
        enrichedSkill.put("NUM_TIMES_INCREASED", skillDetails.getInt32("NUM_TIMES_INCREASED"));
        enrichedSkill.put("LEVEL_FROM_ATTRIBUTES", levelFromAttributes);
        enrichedSkill.put("CURRENT_LEVEL", levelFromAttributes + numTimesIncreased + initialLevel);
        enrichedSkill.put("XP_SPENT", skillDetails.getInt64("XP_SPENT"));
        enrichedSkill.put("SKILL_ADVANCEMENT_CLASS_TYPE", skillDetails.getInt64("SKILL_ADVANCEMENT_CLASS_TYPE"));
        enrichedSkill.put("SKILL_ADVANCEMENT_CLASS_NAME", skillDetails.getString("SKILL_ADVANCEMENT_CLASS_NAME"));
        enrichedSkill.put("LAST_USED_TIMESTAMP", skillDetails.getString("LAST_USED_TIMESTAMP"));
        return enrichedSkill;
    }

    @Override
    public void configure(Map<String, ?> configs) {
        final var portalDatDbImporter = new PortalDatDbImporter();
        skillMap = portalDatDbImporter.getSkillMap();
    }
}
