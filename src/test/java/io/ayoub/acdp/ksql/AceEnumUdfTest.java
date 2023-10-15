package io.ayoub.acdp.ksql;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AceEnumUdfTest {
    private final AceEnumUdf udf = new AceEnumUdf();

    @BeforeAll
    void createUdf() {
        udf.configure(Map.of());
    }

    @Test
    void shouldResolveEnumeration() {
        long PROPERTY_INT_STACK_SIZE = 12L;
        assertEquals("StackSize", udf.aceEnum("PropertyInt", PROPERTY_INT_STACK_SIZE));
    }

    @Test
    void shouldResolveExtension() {
        long PROPERTY_INT_ARMOR_TYPE = 27L;
        long ARMOR_TYPE_LEATHER = 2L;
        assertEquals("Leather", udf.aceEnumExtension("PropertyInt", PROPERTY_INT_ARMOR_TYPE, ARMOR_TYPE_LEATHER));
    }

    @Test
    void shouldReturnNullWhenEnumNotFound() {
        assertNull(udf.aceEnum("PropertyIntX", 1L));
    }

    @Test
    void shouldReturnNullWhenIdNotFound() {
        assertNull(udf.aceEnum("PropertyInt", 10_001L));
    }

    @Test
    void shouldReturnNullWhenExtensionNotFound() {
        // 0 is not extended
        assertNull(udf.aceEnumExtension("PropertyInt", 0L, 1L));
        // 1 is extended (ItemType), but 3 is not a valid value for ItemType
        assertNull(udf.aceEnumExtension("PropertyInt", 1L, 3L));
        // this enumeration has no extensions
        assertNull(udf.aceEnumExtension("AccessLevel", 1L, 1L));
    }
}
