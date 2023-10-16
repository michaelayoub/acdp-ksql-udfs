package io.ayoub.acdp.data.enumerations;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EnumerationDbImporterTest {
    EnumerationDbImporter enumerationDbImporter;
    Map<String, Map<Long, String>> enumerations;
    Map<String, Map<Long, String>> extensions;

    @BeforeAll
    void createImporter() {
        enumerationDbImporter = new EnumerationDbImporter();
        enumerations = enumerationDbImporter.getEnumerations();
        extensions = enumerationDbImporter.getExtensions();
    }

    @Test
    void shouldHavePropertyInt() {
        final var propertyIntMap = enumerations.get("PropertyInt");
        long PROPERTY_INT_STACK_SIZE = 12L;
        final var stackSize = propertyIntMap.get(PROPERTY_INT_STACK_SIZE);
        assertEquals("StackSize", stackSize);
    }

    @Test
    void shouldFollowEquipMaskExtension() {
        long PROPERTY_INT_CURRENT_WIELDED_LOCATION = 10L;
        final var extension = extensions.get("PropertyInt").get(PROPERTY_INT_CURRENT_WIELDED_LOCATION);
        assertEquals("EquipMask", extension);
    }

    @Test
    void shouldFollowArmorTypeExtension() {
        long PROPERTY_INT_ARMOR_TYPE = 27L;
        final var extension = extensions.get("PropertyInt").get(PROPERTY_INT_ARMOR_TYPE);
        long ARMOR_TYPE_LEATHER = 2L;
        final var extended = enumerations.get(extension).get(ARMOR_TYPE_LEATHER);
        assertEquals("Leather", extended);
    }
}
