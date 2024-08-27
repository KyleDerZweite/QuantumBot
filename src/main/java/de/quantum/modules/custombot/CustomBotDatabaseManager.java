package de.quantum.modules.custombot;

import de.quantum.core.BotType;
import de.quantum.core.database.DatabaseManager;
import de.quantum.core.utils.CheckUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.sql.*;

@Slf4j
public class CustomBotDatabaseManager {

    public static final String DATABASE_NAME = "custom_bots";

    // Method to ensure database table exists
    public static boolean ensureTableExists() {
        try {
            Connection connection = DatabaseManager.getInstance().getConnection();
            Statement statement = connection.createStatement();

            String createTableQuery = "CREATE TABLE IF NOT EXISTS " + DATABASE_NAME + " (" +
                    "bot_id VARCHAR(32), " +
                    "guild_id VARCHAR(32), " +
                    "active BOOLEAN, " +
                    "verified BOOLEAN " +
                    ")";

            statement.execute(createTableQuery);
            statement.close();
        } catch (SQLException e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    public static void addCustomBot(@NotNull String botId, @NotNull String encryptedToken, String guildId) {
        addCustomBot(botId, encryptedToken, guildId, true, true);
    }

    public static void addCustomBot(@NotNull String botId, @NotNull String encryptedToken, String guildId, boolean isActive, boolean isVerified) {
        if (!ensureTableExists()) return;
        DatabaseManager.getInstance().insertNewBot(botId, encryptedToken, BotType.CUSTOM);
        String sql = "INSERT INTO " + DATABASE_NAME + " (bot_id, guild_id, active, verified) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, botId);
            stmt.setString(2, guildId);
            stmt.setBoolean(3, isActive);
            stmt.setBoolean(3, isVerified);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static boolean guildHasCustomBot(@NotNull String guildId) {
        ResultSet rs = DatabaseManager.getInstance().selectFromWhere("*", DATABASE_NAME, "guild_id", guildId);
        if (CheckUtils.checkNull(rs)) {
            return false;
        }
        try {
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            log.debug(e.getMessage(), e);
        }
        return false;
    }

    public static boolean isCustomBot(String botId) {
        ResultSet rs = DatabaseManager.getInstance().selectFromWhere("*", DATABASE_NAME, "bot_id", botId);
        if (CheckUtils.checkNull(rs)) {
            return false;
        }
        try {
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            log.debug(e.getMessage(), e);
        }
        return false;
    }

}
