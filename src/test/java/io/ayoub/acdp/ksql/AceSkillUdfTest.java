package io.ayoub.acdp.ksql;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AceSkillUdfTest {
    private final AceSkillUdf udf = new AceSkillUdf();

    @BeforeAll
    void createUdf() { udf.configure(Map.of()); }

    @Test
    void calculatesWarMagic() {
        final int WAR_MAGIC_TYPE = 34;
        List<Long> attributes = List.of(200L, 290L, 200L, 200L, 290L, 290L);
        List<Integer> attributeKeys = List.of(1, 2, 3, 4, 5, 6);

        int warMagicContrib = udf.aceSkillFromAttributes(WAR_MAGIC_TYPE, attributes, attributeKeys);
        assertEquals(Math.ceil((290+290)/4.0), warMagicContrib);
    }

    @Test
    void orderOfAttributesDoesNotMatter() {
        final int WAR_MAGIC_TYPE = 34;
        List<Long> attributes = List.of(200L, 290L, 200L, 290L, 290L, 200L);
        List<Integer> attributeKeys = List.of(1, 2, 3, 6, 5, 4);

        int warMagicContrib = udf.aceSkillFromAttributes(WAR_MAGIC_TYPE, attributes, attributeKeys);
        assertEquals(Math.ceil((290+290)/4.0), warMagicContrib);
    }

    @Test
    void singleAttributeSkill() {
        final int RUN_TYPE = 24;
        List<Long> attributes = List.of(200L, 290L, 200L, 200L, 290L, 290L);
        List<Integer> attributeKeys = List.of(1, 2, 3, 4, 5, 6);

        int runContrib = udf.aceSkillFromAttributes(RUN_TYPE, attributes, attributeKeys);
        assertEquals(200, runContrib);
    }

    @Test
    void singleAttributeSkillWithDivisor() {
        final int ARCANE_LORE_TYPE = 14;
        List<Long> attributes = List.of(200L, 290L, 200L, 200L, 290L, 290L);
        List<Integer> attributeKeys = List.of(1, 2, 3, 4, 5, 6);

        int arcaneLoreContrib = udf.aceSkillFromAttributes(ARCANE_LORE_TYPE, attributes, attributeKeys);
        assertEquals(Math.ceil(290 / 3.0), arcaneLoreContrib);
    }
}
