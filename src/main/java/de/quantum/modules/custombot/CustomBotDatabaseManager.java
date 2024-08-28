package de.quantum.modules.custombot;

import de.quantum.core.BotType;
import de.quantum.core.database.DatabaseManager;
import de.quantum.core.utils.CheckUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;

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
                    "activity_type_id INTEGER, " +
                    "activity_name VARCHAR(128), " +
                    "activity_url VARCHAR(128), " +
                    "active BOOLEAN, " +
                    "verified BOOLEAN " +
                    ")";

            statement.execute(createTableQuery);
            statement.close();
        } catch (SQLException e) {
            log.error(e.getMessage());
            return true;
        }
        return false;
    }

    public static void addCustomBot(@NotNull String botId, @NotNull String encryptedToken, String guildId) {
        addCustomBot(botId, encryptedToken, guildId, 0, "",null, true, true);
    }

    public static void addCustomBot(
            @NotNull String botId, @NotNull String encryptedToken,
            String guildId, int activityTypeId, String activityName,
            @Nullable String activityUrl,
            boolean isActive, boolean isVerified) {
        if (ensureTableExists()) return;
        DatabaseManager.getInstance().insertNewBot(botId, encryptedToken, BotType.CUSTOM);
        String sql = "INSERT INTO " + DATABASE_NAME + " (bot_id, guild_id, activity_type_id, activity_name, activity_url, active, verified) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, botId);
            stmt.setString(2, guildId);
            stmt.setInt(3, activityTypeId);
            stmt.setString(4, activityName);
            stmt.setString(5, activityUrl);
            stmt.setBoolean(6, isActive);
            stmt.setBoolean(7, isVerified);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static boolean guildHasCustomBot(@NotNull String guildId) {
        if (ensureTableExists()) return false;
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
        if (ensureTableExists()) return false;
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

    public static void updateActivity(String botId, int activityTypeId, String activityName, String activityUrl) {
        if (ensureTableExists()) return;
        String sql = "UPDATE " + DATABASE_NAME + " SET activity_type_id = ?, activity_name = ?, activity_url = ? WHERE bot_id = ?";
        try (PreparedStatement stmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setInt(1, activityTypeId);
            stmt.setString(2, activityName);
            stmt.setString(3, activityUrl);
            stmt.setString(4, botId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static Activity getCustomBotActivity(@NotNull String botId) {
        if (ensureTableExists()) return null;
        ResultSet rs = DatabaseManager.getInstance().selectFromWhere("activity_type_id, activity_name, activity_url", DATABASE_NAME, "bot_id", botId);
        if (CheckUtils.checkNull(rs)) {
            return null;
        }
        try {
            if (rs.next()) {
                int activityTypeId = rs.getInt("activity_type_id");
                String activityName = rs.getString("activity_name");
                String activityUrl = rs.getString("activity_url");
                return Activity.of(Activity.ActivityType.fromKey(activityTypeId), activityName, activityUrl);
            }
        } catch (SQLException e) {
            log.debug(e.getMessage(), e);
        }
        return null;
    }

    @NotNull
    public static ArrayList<String> getCustomBotTokens() {
        ArrayList<String> customBotTokens = new ArrayList<>();
        if (ensureTableExists()) return customBotTokens;
        ResultSet rs = DatabaseManager.getInstance().selectFrom("*", DATABASE_NAME);
        if (CheckUtils.checkNull(rs)) {
            return customBotTokens;
        }

        try {
            while (rs.next()) {
                String botId = rs.getString("bot_id");
                boolean isActive = rs.getBoolean("active");
                boolean isVerified = rs.getBoolean("verified");
                if (!isActive || !isVerified) continue;
                ResultSet botTokenSet = DatabaseManager.getInstance().selectFromWhere("token", DatabaseManager.BOT_TABLE_NAME, "bot_id", botId);
                if (botTokenSet.next()) {
                    String botToken = botTokenSet.getString("token");
                    customBotTokens.add(botToken);
                }
                botTokenSet.close();
            }
            rs.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return customBotTokens;
    }

    public static String getCustomBotToken(@NotNull String botId) {
        if (ensureTableExists()) return null;
        ResultSet rs = DatabaseManager.getInstance().selectFromWhere("active, verified", DATABASE_NAME, "bot_id", botId);
        if (CheckUtils.checkNull(rs)) {
            return null;
        }
        try {
            if (rs.next()) {
                boolean isActive = rs.getBoolean("active");
                boolean isVerified = rs.getBoolean("verified");
                if (!isActive || !isVerified) return null;
                ResultSet botTokenSet = DatabaseManager.getInstance().selectFromWhere("token", DatabaseManager.BOT_TABLE_NAME, "bot_id", botId);
                if (botTokenSet.next()) {
                    String token = botTokenSet.getString("token");
                    rs.close();
                    botTokenSet.close();
                    return token;
                }
                botTokenSet.close();
            }
            rs.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}

