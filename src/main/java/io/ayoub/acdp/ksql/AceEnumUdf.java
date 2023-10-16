package io.ayoub.acdp.ksql;

import io.ayoub.acdp.data.enumerations.EnumerationDbImporter;
import io.confluent.ksql.function.udf.Udf;
import io.confluent.ksql.function.udf.UdfDescription;
import io.confluent.ksql.function.udf.UdfParameter;
import org.apache.kafka.common.Configurable;

import java.util.HashMap;
import java.util.Map;

@UdfDescription(
        name = "ace_enum",
        author = "Michael Ayoub",
        version = "0.1",
        description = "A function for converting ACE Enum values to names."
)
public class AceEnumUdf implements Configurable {
    private Map<String, Map<Long, String>> enumerations = new HashMap<>();
    private Map<String, Map<Long, String>> extensions = new HashMap<>();

    @Udf(description = "Return the label for a given ACE Enumeration.")
    public String aceEnum(@UdfParameter String enumName, @UdfParameter long enumId) {
        if (enumerations.get(enumName) != null) {
            return enumerations.get(enumName).get(enumId);
        } else {
            return null;
        }
    }

    @Udf(description = "Return the extended value for a given ACE Enumeration value.")
    public String aceEnumExtension(@UdfParameter String enumName, @UdfParameter long enumId, @UdfParameter long enumValue) {
        try {
            final var extension = extensions.get(enumName).get(enumId);
            return enumerations.get(extension).get(enumValue);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public void configure(Map<String, ?> configs) {
        final var enumerationDbImporter = new EnumerationDbImporter();
        enumerations = enumerationDbImporter.getEnumerations();
        extensions = enumerationDbImporter.getExtensions();
    }
}
