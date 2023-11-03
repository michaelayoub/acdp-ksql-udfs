package io.ayoub.acdp.ksql;

import io.confluent.ksql.function.udaf.Udaf;
import io.confluent.ksql.function.udaf.UdafDescription;
import io.confluent.ksql.function.udaf.UdafFactory;
import io.confluent.ksql.function.udf.UdfDescription;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@UdafDescription(
        name = "ace_skill_collect",
        author = "Michael Ayoub",
        version = "0.1",
        description = "Collect rows for individual skills and produce a " +
                "single array of skills sorted by type."
)
public class AceSkillUdaf {
    public static final Schema SKILL_PARAM_SCHEMA = AceSkillUdf.SKILL_RETURN_SCHEMA;
    public static final String SKILL_PARAM_SCHEMA_DESCRIPTOR = AceSkillUdf.SKILL_RETURN_SCHEMA_DESCRIPTOR;

    public static final String AGGREGATE_SCHEMA_DESCRIPTOR =
            "MAP<STRING, " + SKILL_PARAM_SCHEMA_DESCRIPTOR + ">";

    public static final Schema RETURN_INNER_SCHEMA = SKILL_PARAM_SCHEMA;
    public static final String RETURN_SCHEMA_DESCRIPTOR =
            "ARRAY<" + SKILL_PARAM_SCHEMA_DESCRIPTOR + ">";

    @UdafFactory(
            description = "Collect the latest skill values",
            paramSchema = SKILL_PARAM_SCHEMA_DESCRIPTOR,
            aggregateSchema = AGGREGATE_SCHEMA_DESCRIPTOR,
            returnSchema = RETURN_SCHEMA_DESCRIPTOR
    )
    public static Udaf<Struct, Map<String, Struct>, List<Struct>> createUdaf() { return new AceSkillUdafImpl(); }

    static class AceSkillUdafImpl implements Udaf<Struct, Map<String, Struct>, List<Struct>> {
        private static DecimalFormat dc = new DecimalFormat("00");

        public static Map<String, Struct> newAggregateValue() {
            var map = new TreeMap<String, Struct>();

            return map;
        }

        @Override
        public Map<String, Struct> initialize() {
            return newAggregateValue();
        }

        @Override
        public Map<String, Struct> aggregate(Struct newValue, Map<String, Struct> aggregateValue) {
            aggregateValue.put(dc.format(newValue.getInt32("PROPERTY_TYPE")), newValue);

            return aggregateValue;
        }

        @Override
        public Map<String, Struct> merge(Map<String, Struct> aggOne, Map<String, Struct> aggTwo) {
            return aggTwo;
        }

        @Override
        public List<Struct> map(Map<String, Struct> agg) {
            return new ArrayList<>(agg.values());
        }
    }
}
