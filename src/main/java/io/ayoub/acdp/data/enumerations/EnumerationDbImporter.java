package io.ayoub.acdp.data.enumerations;

import io.ayoub.acdp.proto.EnumerationDBOuterClass.EnumerationDB;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EnumerationDbImporter {
    private final Map<String, Map<Long, String>> enumerations = new HashMap<>();
    private final Map<String, Map<Long, String>> extensions = new HashMap<>();

    public EnumerationDbImporter() {
        try {
            final EnumerationDB enumerationDB = EnumerationDB
                    .parseFrom(getClass().getClassLoader().getResourceAsStream("enums.binpb"));
            loadEnumerations(enumerationDB);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadEnumerations(
            EnumerationDB enumerationDB) {
        enumerationDB.getEnumsList().forEach(enumeration -> {
            Map<Long, String> destinationEnumeration = new HashMap<>();
            Map<Long, String> destinationExtension = new HashMap<>();

            enumeration.getValuesList().forEach(enumerationValue -> {
                if (enumerationValue.getExtension().length() > 0) {
                    destinationExtension.put(enumerationValue.getValue(), enumerationValue.getExtension());
                }

                destinationEnumeration.put(enumerationValue.getValue(), enumerationValue.getLabel());
            });

            enumerations.put(enumeration.getName(), destinationEnumeration);
            if (destinationExtension.size() > 0) {
                extensions.put(enumeration.getName(), destinationExtension);
            }
        });
    }

    public Map<String, Map<Long, String>> getEnumerations() {
        return enumerations;
    }

    public Map<String, Map<Long, String>> getExtensions() {
        return extensions;
    }
}
