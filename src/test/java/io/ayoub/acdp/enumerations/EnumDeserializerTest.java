package io.ayoub.acdp.enumerations;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EnumDeserializerTest {
    private EnumDeserializer enumDeserializer;
    @BeforeAll
    void createDeserializer() {
        this.enumDeserializer = new EnumDeserializer();
    }

    @Test
    void shouldCreate() { assertNotNull(enumDeserializer); }

    @Test
    void shouldHavePropertyInt() {
        final var propertyIntMap = enumDeserializer.getEnumerations().get("PropertyInt");
        long PROPERTY_INT_STACK_SIZE = 12L;
        final var stackSize = propertyIntMap.get(PROPERTY_INT_STACK_SIZE);
        assertEquals("StackSize", stackSize);
    }

    @Test
    void shouldFollowEquipMaskExtension() {
        long PROPERTY_INT_CURRENT_WIELDED_LOCATION = 10L;
        final var extension = enumDeserializer.getExtensions().get("PropertyInt").get(PROPERTY_INT_CURRENT_WIELDED_LOCATION);
        assertEquals("EquipMask", extension);
    }

    @Test
    void shouldFollowArmorTypeExtension() {
        long PROPERTY_INT_ARMOR_TYPE = 27L;
        final var extension = enumDeserializer.getExtensions().get("PropertyInt").get(PROPERTY_INT_ARMOR_TYPE);
        long ARMOR_TYPE_LEATHER = 2L;
        final var extended = enumDeserializer.getEnumerations().get(extension).get(ARMOR_TYPE_LEATHER);
        assertEquals("Leather", extended);
    }
}
