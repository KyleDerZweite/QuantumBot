package de.quantum.core.database;

import de.quantum.core.BotType;
import de.quantum.core.utils.CheckUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;

@Slf4j
public class DatabaseManager {
    private static volatile DatabaseManager INSTANCE = null;

    @Getter
    private Connection connection = null;

    public static final String BOT_TABLE_NAME = "bots";

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

    public ResultSet selectQuery(String query) {
        ResultSet rs = executeQuery(query);
        if (CheckUtils.checkNull(rs)) {
            log.warn("{} returned null", query);
            return null;
        }
        return rs;
    }

    public ResultSet selectFrom(String selectStr, String tableStr) {
        String queryString = "SELECT %s FROM %s".formatted(selectStr, tableStr);
        return selectQuery(queryString);
    }

    public ResultSet selectFromWhere(String selectStr, String tableStr, String whereConditionStr, String whereIdentifierStr) {
        String queryString = "SELECT %s FROM %s WHERE %s='%s'".formatted(selectStr, tableStr, whereConditionStr, whereIdentifierStr);
        return selectQuery(queryString);
    }

    public ArrayList<String> getVerifiedTokens() {
        ResultSet rs = selectFrom("token", BOT_TABLE_NAME);
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

    public void insertNewBot(String botId, String encryptedToken, BotType botType) {
        String sql = "INSERT INTO " + BOT_TABLE_NAME +" (bot_id, token, type) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = DatabaseManager.getInstance().getConnection().prepareStatement(sql)) {
            stmt.setString(1, botId);
            stmt.setString(2, encryptedToken);
            stmt.setInt(3, botType.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }




}