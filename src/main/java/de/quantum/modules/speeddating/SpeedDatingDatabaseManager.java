package de.quantum.modules.speeddating;

import de.quantum.core.database.DatabaseManager;
import de.quantum.modules.speeddating.entities.SpeedDatingConfig;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpeedDatingDatabaseManager {

    public static final String DATABASE_NAME = "speed_dating";

    private static final String INSERT_STATEMENT = "INSERT INTO " + DATABASE_NAME + " (" +
            "bot_id, guild_id, category_id, voice_channel_id, duration_seconds" +
            ") VALUES (?, ?, ?, ?, ?)";

    // Method to ensure database table exists
    public static boolean ensureTableExists() {
        try {
            Statement statement = DatabaseManager.getInstance().getStatement();

            String createTableQuery = "CREATE TABLE IF NOT EXISTS " + DATABASE_NAME + " (" +
                    "bot_id VARCHAR(32), " +
                    "guild_id VARCHAR(32), " +
                    "category_id VARCHAR(32), " +
                    "voice_channel_id VARCHAR(32), " +
                    "duration_seconds INTEGER " +
                    ")";

            statement.execute(createTableQuery);
            statement.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            return true;
        }
        return false;
    }

    public static ConcurrentHashMap<String, SpeedDatingConfig> getSpeedDatingConfigs() {
        ConcurrentHashMap<String, SpeedDatingConfig> configs = new ConcurrentHashMap<>();
        if (ensureTableExists()) return configs;
        try {
            Connection connection = DatabaseManager.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + DATABASE_NAME);
            while (resultSet.next()) {
                SpeedDatingConfig config = SpeedDatingConfig.fromResultSet(resultSet);
                configs.put(config.guild().getId(), config);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return configs;
    }

    public static SpeedDatingConfig getSpeedDatingConfig(String guildId) {
        if (ensureTableExists()) return null;
        try {
            Connection connection = DatabaseManager.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = DatabaseManager.getInstance().selectFromWhere("*", DATABASE_NAME, "guild_id", guildId);
            if (resultSet.next()) {
                return SpeedDatingConfig.fromResultSet(resultSet);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    public static boolean containsSpeedDatingConfig(String guildId) {
        boolean contains = false;
        try {
            Connection connection = DatabaseManager.getInstance().getConnection();
            PreparedStatement checkStatement = connection.prepareStatement("SELECT 1 FROM " + DATABASE_NAME + " WHERE guild_id = ?");
            checkStatement.setString(1, guildId);
            ResultSet resultSet = checkStatement.executeQuery();
            contains = resultSet.next();
            checkStatement.close();
            resultSet.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return contains;
    }

    public static void insertSpeedDatingConfig(String botId, String guildId, String categoryId, String voiceChannelId, int durationSeconds) {
        if (ensureTableExists()) return;
        Connection connection = DatabaseManager.getInstance().getConnection();
        try {
            // Check if the guild ID already exists in the table
            if (containsSpeedDatingConfig(guildId)) {
                // Guild ID already exists, update the config
                PreparedStatement updateStatement = connection.prepareStatement("UPDATE " + DATABASE_NAME + " SET bot_id = ?, category_id = ?, voice_channel_id = ?, duration_seconds = ? WHERE guild_id = ?");
                updateStatement.setString(1, botId);
                updateStatement.setString(2, categoryId);
                updateStatement.setString(3, voiceChannelId);
                updateStatement.setInt(4, durationSeconds);
                updateStatement.setString(5, guildId);
                updateStatement.executeUpdate();
                updateStatement.close();
            } else {
                // Guild ID does not exist, insert a new config
                PreparedStatement insertStatement = connection.prepareStatement(INSERT_STATEMENT);
                insertStatement.setString(1, botId);
                insertStatement.setString(2, guildId);
                insertStatement.setString(3, categoryId);
                insertStatement.setString(4, voiceChannelId);
                insertStatement.setInt(5, durationSeconds);
                insertStatement.executeUpdate();
                insertStatement.close();
            }
            // Close the statements and result set

        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static int getDuration(String guildId) {
        if (ensureTableExists()) return 300;

        Connection connection = DatabaseManager.getInstance().getConnection();
        try {
            // Prepare a query to retrieve the duration for the given guild ID
            PreparedStatement statement = connection.prepareStatement("SELECT duration_seconds FROM " + DATABASE_NAME + " WHERE guild_id = ?");
            statement.setString(1, guildId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                // Guild ID exists, return the duration
                return resultSet.getInt("duration_seconds");
            }
            statement.close();
        } catch (SQLException e) {
            log.warn(e.getMessage(), e);
            return 300; // or throw an exception, depending on your error handling strategy
        }
        return 300;
    }

    public static String getVoiceChannelId(String guildId) {
        if (ensureTableExists()) return null;

        Connection connection = DatabaseManager.getInstance().getConnection();
        try {
            // Prepare a query to retrieve the voice channel ID for the given guild ID
            PreparedStatement statement = connection.prepareStatement("SELECT voice_channel_id FROM  " + DATABASE_NAME + " WHERE guild_id = ?");
            statement.setString(1, guildId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("voice_channel_id");
            }
            statement.close();
        } catch (SQLException e) {
            log.warn(e.getMessage(), e);
        }
        return null;
    }

    public static String getCategoryId(String guildId) {
        if (ensureTableExists()) return null;

        Connection connection = DatabaseManager.getInstance().getConnection();
        try {
            // Prepare a query to retrieve the category ID for the given guild ID
            PreparedStatement statement = connection.prepareStatement("SELECT category_id FROM " + DATABASE_NAME + " WHERE guild_id = ?");
            statement.setString(1, guildId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("category_id");
            }
            statement.close();
        } catch (SQLException e) {
            log.warn(e.getMessage(), e);
        }
        return null;
    }


}
