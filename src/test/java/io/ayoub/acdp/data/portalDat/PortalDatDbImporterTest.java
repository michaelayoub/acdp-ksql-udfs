package io.ayoub.acdp.data.portalDat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PortalDatDbImporterTest {
    PortalDatDbImporter portalDatDbImporter;

    @BeforeAll
    void createImporter() {
        portalDatDbImporter = new PortalDatDbImporter();
    }

    @Test
    void shouldHave275Levels() {
        int MAX_LEVEL = 275;
        assertEquals(MAX_LEVEL, portalDatDbImporter.getCharacterLevelXp().size());
    }

    @Test
    void shouldTake1MillionXpForLevel20() {
        long XP_FOR_LEVEL_20 = 1084206;
        assertEquals(XP_FOR_LEVEL_20, portalDatDbImporter.getCharacterLevelXp().get(19));
    }

    @Test
    void shouldUseFocusForSalvaging() {
        int FOCUS_ATTR_TYPE = 4;
        int SALVAGING_SKILL_TYPE = 40;
        assertEquals(FOCUS_ATTR_TYPE, portalDatDbImporter.getSkillMap().get(SALVAGING_SKILL_TYPE).getAttr1());
    }

    @Test
    void shouldDivideMissleWeaponsBy2() {
        int MISSILE_WEAPONS_DIVISOR = 2;
        int MISSILE_WEAPONS_TYPE = 47;
        assertEquals(MISSILE_WEAPONS_DIVISOR, portalDatDbImporter.getSkillMap().get(MISSILE_WEAPONS_TYPE).getDivisor());
    }
}
