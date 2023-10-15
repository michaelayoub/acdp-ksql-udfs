package io.ayoub.acdp.portalDatabase;

import io.ayoub.acdp.portalDatabase.entities.SkillBase;
import io.ayoub.acdp.portalDatabase.tables.SkillTable;
import io.ayoub.acdp.portalDatabase.tables.XPTable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class PortalDatabase {
    private SkillTable skillTable;
    private XPTable xpTable;

    public PortalDatabase() {
        try {
            final var skillBaseMapObjectInputStream = new ObjectInputStream(getClass().getClassLoader().getResourceAsStream("skillTable.dat"));
            final var xpTableObjectInputStream = new ObjectInputStream(getClass().getClassLoader().getResourceAsStream("xpTable.dat"));
            this.skillTable = (SkillTable) skillBaseMapObjectInputStream.readObject();
            this.xpTable = (XPTable) xpTableObjectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public XPTable getXPTable() {
        return this.xpTable;
    }

    public SkillTable getSkillTable() {
        return skillTable;
    }
}
