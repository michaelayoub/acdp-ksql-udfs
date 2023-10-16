package io.ayoub.acdp.ksql;

import io.confluent.ksql.function.udaf.Udaf;
import io.confluent.ksql.function.udaf.UdafDescription;
import io.confluent.ksql.function.udaf.UdafFactory;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import java.util.List;
import java.util.Optional;

@UdafDescription(
        name = "ace_attribute_collect",
        author = "Michael Ayoub",
        version = "0.1",
        description = "Collect rows for individual attributes and produce a " +
                "single array of attributes sorted by type."
)
public class AceAttributeUdaf {
    public static final Schema PARAM_SCHEMA = SchemaBuilder
            .struct().optional()
            .field("ATTRIBUTE_TYPE", Schema.OPTIONAL_INT32_SCHEMA)
            .field("ATTRIBUTE_NAME", Schema.OPTIONAL_STRING_SCHEMA)
            .field("ATTRIBUTE_VALUE", Schema.OPTIONAL_INT64_SCHEMA)
            .build();

    public static final String PARAM_SCHEMA_DESCRIPTOR =
            "STRUCT<" +
                    "ATTRIBUTE_TYPE INTEGER, " +
                    "ATTRIBUTE_NAME VARCHAR(STRING), " +
                    "ATTRIBUTE_VALUE BIGINT" +
                    ">";

    public static final Schema AGGREGATE_SCHEMA = SchemaBuilder
            .struct().optional()
            .field("STRENGTH", Schema.OPTIONAL_INT64_SCHEMA)
            .field("ENDURANCE", Schema.OPTIONAL_INT64_SCHEMA)
            .field("QUICKNESS", Schema.OPTIONAL_INT64_SCHEMA)
            .field("COORDINATION", Schema.OPTIONAL_INT64_SCHEMA)
            .field("FOCUS", Schema.OPTIONAL_INT64_SCHEMA)
            .field("SELF", Schema.OPTIONAL_INT64_SCHEMA)
            .build();

    public static final String AGGREGATE_SCHEMA_DESCRIPTOR =
            "STRUCT<" +
                    "STRENGTH BIGINT, " +
                    "ENDURANCE BIGINT, " +
                    "QUICKNESS BIGINT, " +
                    "COORDINATION BIGINT, " +
                    "FOCUS BIGINT, " +
                    "SELF BIGINT" +
                    ">";

    @UdafFactory(
            description = "Collect the latest attribute values",
            paramSchema = PARAM_SCHEMA_DESCRIPTOR,
            aggregateSchema = AGGREGATE_SCHEMA_DESCRIPTOR
    )
    public static Udaf<Struct, Struct, List<Long>> createUdaf() {
        return new AceAttributeUdafImpl();
    }

    private static class AceAttributeUdafImpl implements Udaf<Struct, Struct, List<Long>> {

        @Override
        public Struct initialize() {
            return new Struct(AGGREGATE_SCHEMA);
        }

        @Override
        public Struct aggregate(Struct newValue, Struct aggregateValue) {
            String attributeName = newValue.getString("ATTRIBUTE_NAME").toUpperCase();
            long attributeValue = newValue.getInt64("ATTRIBUTE_VALUE");

            aggregateValue.put(attributeName, attributeValue);

            return aggregateValue;
        }

        @Override
        public Struct merge(Struct aggOne, Struct aggTwo) {
            return aggTwo;
        }

        @Override
        public List<Long> map(Struct intermediate) {
            return List.of(
                    Optional.ofNullable(intermediate.getInt64("STRENGTH")).orElse(0L),
                    Optional.ofNullable(intermediate.getInt64("ENDURANCE")).orElse(0L),
                    Optional.ofNullable(intermediate.getInt64("QUICKNESS")).orElse(0L),
                    Optional.ofNullable(intermediate.getInt64("COORDINATION")).orElse(0L),
                    Optional.ofNullable(intermediate.getInt64("FOCUS")).orElse(0L),
                    Optional.ofNullable(intermediate.getInt64("SELF")).orElse(0L)
            );
        }
    }
}
