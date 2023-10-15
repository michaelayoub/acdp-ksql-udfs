package io.ayoub.acdp.portalDatabase;

import io.ayoub.acdp.portalDatabase.entities.SkillBase;
import io.ayoub.acdp.portalDatabase.tables.SkillTable;
import io.ayoub.acdp.portalDatabase.tables.XPTable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PortalDbImporter {
    public PortalDbImporter(String portalDatDbFile, XPTable xpTable, SkillTable skillTable) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + portalDatDbFile)) {
            xpTable.setCharacterLevelXPList(extractLongXPList(connection, "character_level_xp_list"));
            xpTable.setAttributeXPList(extractIntXPList(connection, "attribute_xp_list"));
            xpTable.setVitalXPList(extractIntXPList(connection, "vital_xp_list"));
            xpTable.setTrainedSkillXPList(extractIntXPList(connection, "trained_skill_xp_list"));
            xpTable.setSpecializedSkillXPList(extractIntXPList(connection, "specialized_skill_xp_list"));

            skillTable.setSkillBaseMap(extractSkills(connection));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            FileOutputStream skillTableOutputStream = new FileOutputStream("skillTable.dat");
            FileOutputStream xpTableOutputStream = new FileOutputStream("xpTable.dat");
            ObjectOutputStream objectSkillTableOutputStream = new ObjectOutputStream(skillTableOutputStream);
            ObjectOutputStream objectXPTableOutputStream = new ObjectOutputStream(xpTableOutputStream);
            objectSkillTableOutputStream.writeObject(skillTable);
            objectXPTableOutputStream.writeObject(xpTable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<Integer, SkillBase> extractSkills(Connection connection) throws SQLException {
        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM skills");

        Map<Integer, SkillBase> skillBaseMap = new HashMap<>();

        while (rs.next()) {
            int key = rs.getInt("id");
            int attr1 = rs.getInt("attr1");
            int attr2 = rs.getInt("attr2");
            int divisor = rs.getInt("divisor");
            int iconId = rs.getInt("icon_id");
            String description = rs.getString("description");
            String name = rs.getString("name");

            skillBaseMap.put(key, new SkillBase(name, description, iconId, divisor, attr1, attr2));
        }

        return skillBaseMap;
    }

    private List<Integer> extractIntXPList(Connection connection, String tableName) throws SQLException {
        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + tableName);

        List<Integer> xpList = new ArrayList<>();

        while (rs.next()) {
            int xp = rs.getInt("cost");
            xpList.add(xp);
        }

        return xpList;
    }

    private List<Long> extractLongXPList(Connection connection, String tableName) throws SQLException {
        ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM " + tableName);

        List<Long> xpList = new ArrayList<>();

        while (rs.next()) {
            long xp = rs.getLong("cost");
            xpList.add(xp);
        }

        return xpList;
    }

}
