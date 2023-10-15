package io.ayoub.acdp.portalDatabase;

import io.ayoub.acdp.portalDatabase.tables.SkillTable;
import io.ayoub.acdp.portalDatabase.tables.XPTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PortalDbImporterTest {
    private PortalDbImporter portalDbImporter;
    private final XPTable xpTable = new XPTable();
    private final SkillTable skillTable = new SkillTable();

    @BeforeAll
    void createImporter() {
        this.portalDbImporter = new PortalDbImporter("portal_dat.db", xpTable, skillTable);
    }

    @Test
    void shouldCreate() {
        assertNotNull(portalDbImporter);
    }

    @Test
    void shouldHave275Levels() {
        int MAX_LEVEL = 275;
        assertEquals(MAX_LEVEL, xpTable.getCharacterLevelXPList().size());
    }

    @Test
    void shouldTake1MillionXPForLevel20() {
        long XP_FOR_LEVEL_20 = 1084206;
        assertEquals(XP_FOR_LEVEL_20, xpTable.getCharacterLevelXPList().get(19));
    }

    @Test
    void shouldUseFocusForSalvaging() {
        int FOCUS_ATTR_TYPE = 4;
        int SALVAGING_SKILL_TYPE = 40;
        assertEquals(FOCUS_ATTR_TYPE, skillTable.getSkillBaseMap().get(SALVAGING_SKILL_TYPE).getAttr1());
    }

    @Test
    void shouldDivideMissleWeaponsBy2() {
        int MISSILE_WEAPONS_DIVISOR = 2;
        int MISSILE_WEAPONS_TYPE = 47;
        assertEquals(MISSILE_WEAPONS_DIVISOR, skillTable.getSkillBaseMap().get(MISSILE_WEAPONS_TYPE).getDivisor());
    }
}
