package io.ayoub.acdp.ksql;

import io.confluent.ksql.function.udaf.Udaf;
import io.confluent.ksql.function.udaf.UdafFactory;
import io.confluent.ksql.function.udf.UdfDescription;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import java.util.*;
import java.util.stream.Collectors;

@UdfDescription(
        name = "ace_skill_collect",
        author = "Michael Ayoub",
        version = "0.1",
        description = "Collect rows for individual skills and produce a " +
                "single array of skills sorted by type."
)
public class AceSkillUdaf {
    public static final Schema SKILL_PARAM_SCHEMA = AceSkillUdf.SKILL_RETURN_SCHEMA;
    public static final String SKILL_PARAM_SCHEMA_DESCRIPTOR = AceSkillUdf.SKILL_RETURN_SCHEMA_DESCRIPTOR;

    public static final Schema AGGREGATE_SCHEMA = SchemaBuilder.map(
            Schema.INT32_SCHEMA,
            SKILL_PARAM_SCHEMA
    );
    public static final String AGGREGATE_SCHEMA_DESCRIPTOR =
            "MAP<INTEGER, " + SKILL_PARAM_SCHEMA_DESCRIPTOR + ">";

    public static final Schema RETURN_INNER_SCHEMA = SKILL_PARAM_SCHEMA;
    public static final String RETURN_SCHEMA_DESCRIPTOR =
            "ARRAY<" + SKILL_PARAM_SCHEMA_DESCRIPTOR + ">";

    @UdafFactory(
            description = "Collect the latest skill values",
            paramSchema = SKILL_PARAM_SCHEMA_DESCRIPTOR,
            aggregateSchema = AGGREGATE_SCHEMA_DESCRIPTOR,
            returnSchema = RETURN_SCHEMA_DESCRIPTOR
    )
    public static Udaf<Struct, Map<Integer, Struct>, List<Struct>> createUdaf() { return new AceSkillUdafImpl(); }

    static class AceSkillUdafImpl implements Udaf<Struct, Map<Integer, Struct>, List<Struct>> {

        public static Map<Integer, Struct> newAggregateValue() {
            var map = new TreeMap<Integer, Struct>();

            return map;
        }

        @Override
        public Map<Integer, Struct> initialize() {
            return newAggregateValue();
        }

        @Override
        public Map<Integer, Struct> aggregate(Struct newValue, Map<Integer, Struct> aggregateValue) {
            aggregateValue.put(newValue.getInt32("PROPERTY_TYPE"), newValue);

            return aggregateValue;
        }

        @Override
        public Map<Integer, Struct> merge(Map<Integer, Struct> aggOne, Map<Integer, Struct> aggTwo) {
            return aggTwo;
        }

        @Override
        public List<Struct> map(Map<Integer, Struct> agg) {
            return new ArrayList<>(agg.values());
        }
    }
}
