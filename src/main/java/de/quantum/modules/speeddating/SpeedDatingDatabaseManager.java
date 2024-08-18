package de.quantum.modules.speeddating;

import de.quantum.core.database.DatabaseManager;
import de.quantum.modules.audit.entries.AuditEntry;
import de.quantum.modules.speeddating.entities.SpeedDatingConfig;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpeedDatingDatabaseManager {

    public static final String DATABASE_NAME = "speed_dating";

    private static final String INSERT_STATEMENT = "INSERT INTO " + DATABASE_NAME + " (" +
            "bot_id, guild_id, category_id, voice_channel_id" +
            ") VALUES (?, ?, ?, ?)";

    // Method to ensure database table exists
    public static void ensureTableExists() {
        try {
            Connection connection = DatabaseManager.getInstance().getConnection();
            Statement statement = connection.createStatement();

            String createTableQuery = "CREATE TABLE IF NOT EXISTS " + DATABASE_NAME + " (" +
                    "bot_id VARCHAR(32), " +
                    "guild_id VARCHAR(32) PRIMARY KEY, " +
                    "category_id VARCHAR(32), " +
                    "voice_channel_id VARCHAR(32), " +
                    ")";

            statement.execute(createTableQuery);
            statement.close();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    public static ConcurrentHashMap<String, SpeedDatingConfig> getSpeedDatingConfigs() {
        return new ConcurrentHashMap<>();
    }


}
