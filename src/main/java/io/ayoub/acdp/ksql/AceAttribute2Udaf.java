package io.ayoub.acdp.ksql;

import io.confluent.ksql.function.udaf.Udaf;
import io.confluent.ksql.function.udaf.UdafDescription;
import io.confluent.ksql.function.udaf.UdafFactory;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@UdafDescription(
        name = "ace_attribute2_collect",
        author = "Michael Ayoub",
        version = "0.1",
        description = "Collect rows for individual secondary attributes and " +
                "produce a single array of secondary attributes sorted by type."
)
public class AceAttribute2Udaf {
    public static final Schema ATTRIBUTE_2_PARAM_SCHEMA = SchemaBuilder
            .struct().optional()
            .field("PROPERTY_TYPE", Schema.OPTIONAL_INT32_SCHEMA)
            .field("PROPERTY_NAME", Schema.OPTIONAL_STRING_SCHEMA)
            .field("INITIAL_LEVEL", Schema.OPTIONAL_INT64_SCHEMA)
            .field("NUM_TIMES_INCREASED", Schema.OPTIONAL_INT64_SCHEMA)
            .field("XP_SPENT", Schema.OPTIONAL_INT64_SCHEMA)
            .field("CURRENT_LEVEL", Schema.OPTIONAL_INT64_SCHEMA)
            .field("CURRENT_LEVEL_WITH_ENCHANTMENTS", Schema.OPTIONAL_INT64_SCHEMA)
            .build();

    public static final String ATTRIBUTE_2_PARAM_SCHEMA_DESCRIPTOR =
            "STRUCT<PROPERTY_TYPE INTEGER, " +
                    "PROPERTY_NAME VARCHAR(STRING), " +
                    "INITIAL_LEVEL BIGINT, " +
                    "NUM_TIMES_INCREASED BIGINT, " +
                    "XP_SPENT BIGINT, " +
                    "CURRENT_LEVEL BIGINT, " +
                    "CURRENT_LEVEL_WITH_ENCHANTMENTS BIGINT>";

    public static final String AGGREGATE_SCHEMA_DESCRIPTOR =
            "MAP<STRING, " + ATTRIBUTE_2_PARAM_SCHEMA_DESCRIPTOR + ">";

    public static final String RETURN_SCHEMA_DESCRIPTOR =
            "ARRAY<" + ATTRIBUTE_2_PARAM_SCHEMA_DESCRIPTOR + ">";

    @UdafFactory(
            description = "Collect the latest secondary attribute values",
            paramSchema = ATTRIBUTE_2_PARAM_SCHEMA_DESCRIPTOR,
            aggregateSchema = AGGREGATE_SCHEMA_DESCRIPTOR,
            returnSchema = RETURN_SCHEMA_DESCRIPTOR
    )
    public static Udaf<Struct, Map<String, Struct>, List<Struct>> createUdaf() { return new AceAttribute2UdafImpl(); }

    static class AceAttribute2UdafImpl implements Udaf<Struct, Map<String, Struct>, List<Struct>> {
        private static DecimalFormat dc = new DecimalFormat("00");

        public static Map<String, Struct> newAggregateValue() {
            var map = new TreeMap<String, Struct>();

            return map;
        }

        @Override
        public Map<String, Struct> initialize() { return newAggregateValue(); }

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
