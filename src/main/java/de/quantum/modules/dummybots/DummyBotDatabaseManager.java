package de.quantum.modules.dummybots;

import de.quantum.core.database.DatabaseManager;
import de.quantum.core.utils.CheckUtils;
import de.quantum.core.utils.Secret;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DummyBotDatabaseManager {

    public static final String DUMMY_BOT_TABLE_NAME = "dummy_bots";

    public static void addDummyBot(@NotNull String botId, @NotNull String encryptedToken, String guildId) {
        addDummyBot(botId, encryptedToken, guildId, true);
    }

    public static void addDummyBot(@NotNull String botId, @NotNull String encryptedToken, String guildId, boolean isActive) {
        DatabaseManager.getInstance().insertNewBot(botId, encryptedToken);

        String sql = "INSERT INTO " + DUMMY_BOT_TABLE_NAME + " (bot_id, guild_id, active) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, botId);
            stmt.setString(2, guildId);
            stmt.setBoolean(3, isActive);
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    @NotNull
    public static ConcurrentHashMap<String, ArrayList<String>> getDummyBots() {
        ConcurrentHashMap<String, ArrayList<String>> guildDummyTokenMap = new ConcurrentHashMap<>();
        ResultSet rs = DatabaseManager.getInstance().selectFrom("*", "dummy_bots");
        if (CheckUtils.checkNull(rs)) {
            return guildDummyTokenMap;
        }

        try {
            while (rs.next()) {
                String guildId = rs.getString("guild_id");
                if (!guildDummyTokenMap.containsKey(guildId)) {
                    guildDummyTokenMap.put(guildId, new ArrayList<>());
                }
                ArrayList<String> tokens = guildDummyTokenMap.get(guildId);
                String botId = rs.getString("bot_id");
                boolean isActive = rs.getBoolean("active");
                if (!isActive) continue;
                ResultSet botTokenSet = DatabaseManager.getInstance().selectFromWhere("token", DatabaseManager.BOT_TABLE_NAME, "bot_id", botId);
                if (botTokenSet.next()) {
                    String botToken = botTokenSet.getString("token");
                    tokens.add(botToken);
                }
                botTokenSet.close();
            }
            rs.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return guildDummyTokenMap;
    }

    public static ArrayList<String> getGuildDummyBotTokens(String guildId) {
        ConcurrentHashMap<String, ArrayList<String>> dummyBotsMap = getDummyBots();
        return dummyBotsMap.get(guildId);
    }

    @Nullable
    public static String getDummyBotToken(@NotNull String botId) {
        if (!isDummyBot(botId)) {
            return null;
        }
        try (ResultSet botTokenSet = DatabaseManager.getInstance().selectFromWhere("token", DatabaseManager.BOT_TABLE_NAME, "bot_id", botId)) {
            if (botTokenSet.next()) {
                return botTokenSet.getString("token");
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static boolean isDummyBot(String botId) {
        ResultSet rs = DatabaseManager.getInstance().selectFromWhere("*", "dummy_bots", "bot_id", botId);
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
