package io.ayoub.acdp.portalDatabase;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PortalDatabaseTest {
    private PortalDatabase portalDatabase;

    @BeforeAll
    void createDatabase() { this.portalDatabase = new PortalDatabase(); }

    @Test
    void shouldCreate() { assertNotNull(portalDatabase); }

    @Test
    void shouldHave275Levels() {
        int MAX_LEVEL = 275;
        assertEquals(MAX_LEVEL, portalDatabase.getXPTable().getCharacterLevelXPList().size());
    }

    @Test
    void shouldTake1MillionXPForLevel20() {
        long XP_FOR_LEVEL_20 = 1084206;
        assertEquals(XP_FOR_LEVEL_20, portalDatabase.getXPTable().getCharacterLevelXPList().get(19));
    }

    @Test
    void shouldUseFocusForSalvaging() {
        int FOCUS_ATTR_TYPE = 4;
        int SALVAGING_SKILL_TYPE = 40;
        assertEquals(FOCUS_ATTR_TYPE, portalDatabase.getSkillTable().getSkillBaseMap().get(SALVAGING_SKILL_TYPE).getAttr1());
    }

    @Test
    void shouldDivideMissleWeaponsBy2() {
        int MISSILE_WEAPONS_DIVISOR = 2;
        int MISSILE_WEAPONS_TYPE = 47;
        assertEquals(MISSILE_WEAPONS_DIVISOR, portalDatabase.getSkillTable().getSkillBaseMap().get(MISSILE_WEAPONS_TYPE).getDivisor());
    }
}
