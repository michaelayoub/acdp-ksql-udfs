package io.ayoub.acdp.ksql;

import io.ayoub.acdp.model.AttributeType;
import io.ayoub.acdp.model.AttributeTypeValue;
import io.confluent.ksql.function.udaf.Udaf;
import io.confluent.ksql.function.udaf.UdafDescription;
import io.confluent.ksql.function.udaf.UdafFactory;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UdafDescription(
        name = "ace_attribute_collect",
        author = "Michael Ayoub",
        version = "0.1",
        description = "Collect rows for individual attributes and produce a " +
                "single array of attributes sorted by type."
)
public class AceAttributeUdaf {
    public static final Schema ATTRIBUTE_PARAM_SCHEMA = SchemaBuilder
            .struct().optional()
            .field("PROPERTY_TYPE", Schema.OPTIONAL_INT32_SCHEMA)
            .field("PROPERTY_NAME", Schema.OPTIONAL_STRING_SCHEMA)
            .field("INITIAL_LEVEL", Schema.OPTIONAL_INT64_SCHEMA)
            .field("NUM_TIMES_INCREASED", Schema.OPTIONAL_INT64_SCHEMA)
            .field("XP_SPENT", Schema.OPTIONAL_INT64_SCHEMA)
            .field("CURRENT_LEVEL", Schema.OPTIONAL_INT64_SCHEMA)
            .build();

    public static final String ATTRIBUTE_PARAM_SCHEMA_DESCRIPTOR =
            "STRUCT<PROPERTY_TYPE INTEGER, " +
                    "PROPERTY_NAME VARCHAR(STRING), " +
                    "INITIAL_LEVEL BIGINT, " +
                    "NUM_TIMES_INCREASED BIGINT, " +
                    "XP_SPENT BIGINT, " +
                    "CURRENT_LEVEL BIGINT>";


    public static final Schema RETURN_INNER_SCHEMA = ATTRIBUTE_PARAM_SCHEMA;
    public static final String RETURN_SCHEMA_DESCRIPTOR =
            "ARRAY<" + ATTRIBUTE_PARAM_SCHEMA_DESCRIPTOR + ">";


    public static final Schema AGGREGATE_SCHEMA = SchemaBuilder.map(
            Schema.STRING_SCHEMA,
            ATTRIBUTE_PARAM_SCHEMA
    );

    public static final String AGGREGATE_SCHEMA_DESCRIPTOR =
            "MAP<STRING, " + ATTRIBUTE_PARAM_SCHEMA_DESCRIPTOR + ">";

    @UdafFactory(
            description = "Collect the latest attribute values",
            paramSchema = ATTRIBUTE_PARAM_SCHEMA_DESCRIPTOR,
            aggregateSchema = AGGREGATE_SCHEMA_DESCRIPTOR,
            returnSchema = RETURN_SCHEMA_DESCRIPTOR
    )
    public static Udaf<Struct, Map<String, Struct>, List<Struct>> createUdaf() {
        return new AceAttributeUdafImpl();
    }

    static class AceAttributeUdafImpl implements Udaf<Struct, Map<String, Struct>, List<Struct>> {
        public static Map<String, Struct> newAggregateValue() {
            var map = new HashMap<String, Struct>();
            map.put(AttributeType.STRENGTH.getLabel(), newParamStructWithDefault(AttributeType.STRENGTH));
            map.put(AttributeType.ENDURANCE.getLabel(), newParamStructWithDefault(AttributeType.ENDURANCE));
            map.put(AttributeType.QUICKNESS.getLabel(), newParamStructWithDefault(AttributeType.QUICKNESS));
            map.put(AttributeType.COORDINATION.getLabel(), newParamStructWithDefault(AttributeType.COORDINATION));
            map.put(AttributeType.FOCUS.getLabel(), newParamStructWithDefault(AttributeType.FOCUS));
            map.put(AttributeType.SELF.getLabel(), newParamStructWithDefault(AttributeType.SELF));
            return map;
        }

        public static Struct newParamStructWithDefault(AttributeTypeValue attributeTypeValue) {
            return new Struct(ATTRIBUTE_PARAM_SCHEMA)
                    .put("PROPERTY_TYPE", attributeTypeValue.getValue())
                    .put("PROPERTY_NAME", attributeTypeValue.getLabel())
                    .put("CURRENT_LEVEL", 0L);
        }

        @Override
        public Map<String, Struct> initialize() {
            return newAggregateValue();
        }

        @Override
        public Map<String, Struct> aggregate(Struct newValue, Map<String, Struct> aggregateValue) {
            newValue.put("PROPERTY_NAME", newValue.getString("PROPERTY_NAME").toUpperCase());

            String attributeName = newValue.getString("PROPERTY_NAME");
            aggregateValue.put(attributeName, newValue);
            return aggregateValue;
        }

        @Override
        public Map<String, Struct> merge(Map<String, Struct> aggOne, Map<String, Struct> aggTwo) {
            return aggTwo;
        }

        @Override
        public List<Struct> map(Map<String, Struct> intermediate) {
            return List.of(
                    intermediate.get(AttributeType.STRENGTH.getLabel()),
                    intermediate.get(AttributeType.ENDURANCE.getLabel()),
                    intermediate.get(AttributeType.QUICKNESS.getLabel()),
                    intermediate.get(AttributeType.COORDINATION.getLabel()),
                    intermediate.get(AttributeType.FOCUS.getLabel()),
                    intermediate.get(AttributeType.SELF.getLabel())
            );
        }
    }
}
