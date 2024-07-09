package de.quantum.core.database;

import de.quantum.core.utils.CheckUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DatabaseManager {
    private static volatile DatabaseManager INSTANCE = null;

    @Getter
    private Connection connection = null;

    private DatabaseManager() {
        if (INSTANCE != null) {
            throw new AssertionError(
                    "Another instance of "
                            + DatabaseManager.class.getName()
                            + " class already exists, Can't create a new instance.");
        }

    }

    public static DatabaseManager getInstance() {
        if (INSTANCE == null) {
            synchronized (DatabaseManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DatabaseManager();
                }
            }
        }
        return INSTANCE;
    }

    public void init() {
        if (CheckUtils.checkNotNull(connection)) {
            return;
        }
        try {
            var jdbcUrl = DatabaseConfig.getDbUrl();
            var user = DatabaseConfig.getDbUsername();
            var password = DatabaseConfig.getDbPassword();

            if (CheckUtils.checkAnyNull(jdbcUrl, user, password)) {
                log.error("Database connection details are not valid!");
                System.exit(1);
            }
            assert jdbcUrl != null;
            this.connection = DriverManager.getConnection(jdbcUrl, user, password);
            log.info("Database connection established!");
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Statement getStatement() {
        try {
            return this.connection.createStatement();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private ResultSet executeQuery(String query) {
        try {
            Statement statement = getStatement();
            if (CheckUtils.checkNull(statement)) {
                return null;
            }
            return getStatement().executeQuery(query);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private ResultSet selectQuery(String query) {
        ResultSet rs = executeQuery(query);
        if (CheckUtils.checkNull(rs)) {
            log.warn("{} returned null", query);
            return null;
        }
        return rs;
    }

    private ResultSet selectFrom(String selectStr, String tableStr) {
        String queryString = "SELECT %s FROM %s".formatted(selectStr, tableStr);
        return selectQuery(queryString);
    }

    private ResultSet selectFromWhere(String selectStr, String tableStr, String whereConditionStr, String whereIdentifierStr) {
        String queryString = "SELECT %s FROM %s WHERE %s='%s'".formatted(selectStr, tableStr, whereConditionStr, whereIdentifierStr);
        return selectQuery(queryString);
    }

    public ArrayList<String> getVerifiedTokens() {
        ResultSet rs = selectFrom("token", "bots");
        if (CheckUtils.checkNull(rs)) {
            return null;
        }
        ArrayList<String> verifiedTokens = new ArrayList<>();
        try {
            while (rs.next()) {
                String token = rs.getString("token");
                verifiedTokens.add(token);
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return verifiedTokens;
    }

    public long getCommandCooldown(@Nullable Guild guild, String commandId) {
        if (guild == null) {
            // Database query for the default command cooldown
            return 0L;
        }
        // Datebase quere with guild id and command id
        return 1L;
    }


    public ConcurrentHashMap<String, ArrayList<String>> getDummyBots() {
        ResultSet rs = selectFrom("*", "dummy_bots");
        if (CheckUtils.checkNull(rs)) {
            return null;
        }

        ConcurrentHashMap<String, ArrayList<String>> guildDummyTokenMap = new ConcurrentHashMap<>();
        try {
            while (rs.next()) {
                String guildId = rs.getString("guild_id");
                if (!guildDummyTokenMap.containsKey(guildId)) {
                    guildDummyTokenMap.put(guildId, new ArrayList<>());
                }
                ArrayList<String> tokens = guildDummyTokenMap.get(guildId);
                String botId = rs.getString("bot_id");
                ResultSet botTokenSet = selectFromWhere("token", "bots", "bot_id", botId);
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

    public boolean isDummyBot(String botId) {
        ResultSet rs = selectFromWhere("*", "dummy_bots", "bot_id", botId);
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