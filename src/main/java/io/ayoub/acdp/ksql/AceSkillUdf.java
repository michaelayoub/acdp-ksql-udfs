package io.ayoub.acdp.ksql;

import io.ayoub.acdp.data.portalDat.PortalDatDbImporter;
import io.ayoub.acdp.proto.PortalDatDBOuterClass.Skill;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;
import org.apache.kafka.common.Configurable;

import java.util.List;
import java.util.Map;

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

    @Udf
    public int aceSkillFromAttributes(
            @UdfParameter int skillId,
            @UdfParameter List<Long> attributes,
            @UdfParameter List<Integer> attributeKeys
    ) {
        Skill skill = skillMap.get(skillId);

        // TODO: Round, don't use ceil.

        System.out.println("SkillId=" + skillId + ",Attributes="+attributes.toString()+",AttributeKeys="+attributeKeys.toString());

        long attr1Value = attributes.get(attributeKeys.indexOf(skill.getAttr1()));
        double divisor = skill.getDivisor();
        if (skill.getAttr2() == 0) { // single attribute skill
            System.out.println("SkillFromAttributesResult="+(int) Math.ceil(attr1Value / divisor));
            return (int) Math.ceil(attr1Value / divisor);
        } else {
            long attr2Value = attributes.get(attributeKeys.indexOf(skill.getAttr2()));
            System.out.println("SkillFromAttributesResult="+(int) Math.ceil((attr1Value + attr2Value) / divisor));
            return (int) Math.ceil((attr1Value + attr2Value) / divisor);
        }
    }

    @Override
    public void configure(Map<String, ?> configs) {
//        final var enumerationDbImporter = new EnumerationDbImporter();
//        this.enumerations = enumerationDbImporter.getEnumerations();
//        this.extensions = enumerationDbImporter.getExtensions();
        final var portalDatDbImporter = new PortalDatDbImporter();
        skillMap = portalDatDbImporter.getSkillMap();
    }
}
