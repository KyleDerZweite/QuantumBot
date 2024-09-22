package de.quantum.modules.dummybots;

import de.quantum.core.BotType;
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
        DatabaseManager.getInstance().insertNewBot(botId, encryptedToken, BotType.DUMMY);

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
        return getDummyBots(false);
    }

    @NotNull
    public static ConcurrentHashMap<String, ArrayList<String>> getDummyBots(boolean ignoreActive) {
        ConcurrentHashMap<String, ArrayList<String>> guildDummyTokenMap = new ConcurrentHashMap<>();
        ResultSet rs = DatabaseManager.getInstance().selectFrom("*", DUMMY_BOT_TABLE_NAME);
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
                /*
                Active = False && IgnoreActive = false -> continues
                Active = True -> Never continues
                IgnoreActive = True -> Never continues
                Both must be false only then it skips the selection
                 */
                if (!isActive && !ignoreActive) continue;
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

    public static ArrayList<String> getGuildDummyBotTokens(String guildId, boolean ignoreActive) {
        ConcurrentHashMap<String, ArrayList<String>> dummyBotsMap = getDummyBots(ignoreActive);
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
        ResultSet rs = DatabaseManager.getInstance().selectFromWhere("*", DUMMY_BOT_TABLE_NAME, "bot_id", botId);
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
