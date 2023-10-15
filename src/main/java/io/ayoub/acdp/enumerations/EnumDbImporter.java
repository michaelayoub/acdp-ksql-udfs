package io.ayoub.acdp.enumerations;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class EnumDbImporter {

    public EnumDbImporter(String enumDbFile, Map<String, Map<Long, String>> enumerationMaps, Map<String, Map<Long, String>> enumerationExtensionMaps) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + enumDbFile)) {
            ResultSet metaDataResultSet = connection.getMetaData().getTables(null, null, null, null);

            while (metaDataResultSet.next()) {
                final String tableName = metaDataResultSet.getString("TABLE_NAME");
                if (!tableName.startsWith("sqlite")) {
                    final ResultSet enumResultSet = getEnumValues(connection, tableName);
                    final ResultSetMetaData enumMetaData = enumResultSet.getMetaData();

                    Map<Long, String> enumeration = new HashMap<>();
                    if (enumMetaData.getColumnCount() == 3) {
                        Map<Long, String> extension = new HashMap<>();
                        extractWithExtensions(enumResultSet, enumeration, extension);
                        enumerationExtensionMaps.put(tableName, extension);
                    } else {
                        extract(enumResultSet, enumeration);
                    }
                    enumerationMaps.put(tableName, enumeration);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            FileOutputStream enumerationsOutputStream = new FileOutputStream("enumerations.dat");
            FileOutputStream extensionsOutputStream = new FileOutputStream("extensions.dat");
            ObjectOutputStream objectEnumerationsOutputStream = new ObjectOutputStream(enumerationsOutputStream);
            ObjectOutputStream objectExtensionsOutputStream = new ObjectOutputStream(extensionsOutputStream);
            objectEnumerationsOutputStream.writeObject(enumerationMaps);
            objectExtensionsOutputStream.writeObject(enumerationExtensionMaps);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ResultSet getEnumValues(Connection connection, String tableName) throws SQLException {
        return connection.createStatement().executeQuery("SELECT * FROM " + tableName);
    }

    private static void extract(ResultSet enumResultSet, Map<Long, String> enumeration) throws SQLException {
        while (enumResultSet.next()) {
            long value = enumResultSet.getLong("value");
            String label = enumResultSet.getString("label");

            enumeration.put(value, label);
        }
    }

    private static void extractWithExtensions(ResultSet enumResultSet, Map<Long, String> enumeration, Map<Long, String> extensions) throws SQLException {
        while (enumResultSet.next()) {
            long value = enumResultSet.getLong("value");
            String label = enumResultSet.getString("label");
            String extension = enumResultSet.getString("extensionEnum");

            enumeration.put(value, label);
            if (extension != null && extension.length() > 0) {
                extensions.put(value, extension);
            }
        }
    }
}
