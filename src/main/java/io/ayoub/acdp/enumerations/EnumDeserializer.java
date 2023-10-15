package io.ayoub.acdp.enumerations;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class EnumDeserializer {
    private final Map<String, Map<Long, String>> enumerations;
    private final Map<String, Map<Long, String>> extensions;

    public EnumDeserializer() {
        try {
            final var enumerationsObjectInputStream = new ObjectInputStream(getClass().getClassLoader().getResourceAsStream("enumerations.dat"));
            final var extensionsObjectInputStream = new ObjectInputStream(getClass().getClassLoader().getResourceAsStream("extensions.dat"));
            this.enumerations = (HashMap<String, Map<Long, String>>) enumerationsObjectInputStream.readObject();
            this.extensions = (HashMap<String, Map<Long, String>>) extensionsObjectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Map<Long, String>> getEnumerations() {
        return this.enumerations;
    }

    public Map<String, Map<Long, String>> getExtensions() {
        return this.extensions;
    }
}
