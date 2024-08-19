package de.quantum.modules.audit;

import de.quantum.core.database.DatabaseManager;
import de.quantum.modules.audit.entries.AuditEntry;
import de.quantum.modules.audit.entries.DatabaseEntry;
import de.quantum.modules.audit.entries.LogEntry;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class AuditDatabaseManager {

    public static final String DATABASE_NAME = "audit";

    private static final String INSERT_STATEMENT = "INSERT INTO " + DATABASE_NAME + " (" +
            "qid, bot_id, guild_id, member_id, target_id, reason, type_key, member_mention, target_string, change_string, epoch_second" +
            ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


    // Method to ensure database table exists
    public static void ensureTableExists() {
        try {
            Connection connection = DatabaseManager.getInstance().getConnection();
            Statement statement = connection.createStatement();

            String createTableQuery = "CREATE TABLE IF NOT EXISTS " + DATABASE_NAME + " (" +
                    "qid VARCHAR(32) PRIMARY KEY, " +
                    "bot_id VARCHAR(32), " +
                    "guild_id VARCHAR(32), " +
                    "member_id VARCHAR(32), " +
                    "target_id VARCHAR(32), " +
                    "reason VARCHAR(255), " +
                    "type_key INTEGER, " +
                    "member_mention VARCHAR(255), " +
                    "target_string VARCHAR(255), " +
                    "change_string VARCHAR(255), " +
                    "epoch_second BIGINT" +
                    ")";

            statement.execute(createTableQuery);
            statement.close();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    private static void insertAuditEntry(AuditEntry entry) {
        Connection connection = DatabaseManager.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_STATEMENT);
            preparedStatement.setString(1, entry.qid());
            preparedStatement.setString(2, entry.botId());
            preparedStatement.setString(3, entry.guildId());
            preparedStatement.setString(4, entry.memberId());
            preparedStatement.setString(5, entry.targetId());
            preparedStatement.setString(6, entry.reason());
            preparedStatement.setInt(7, entry.typeKey());
            preparedStatement.setString(8, entry.memberMention());
            preparedStatement.setString(9, entry.targetString());
            preparedStatement.setString(10, entry.changeString());
            preparedStatement.setLong(11, entry.epochSecond());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void saveAuditEntries(ConcurrentHashMap<String, ConcurrentHashMap<String, AuditEntry>> auditCache) {
        ensureTableExists();
        auditCache.forEach((guildId, guildCache) -> guildCache.forEach((qid, auditEntry) -> {
            if (auditEntry instanceof LogEntry) {
                insertAuditEntry(auditEntry);
            }
        }));
    }

    public static ConcurrentHashMap<String, ConcurrentHashMap<String, AuditEntry>> getAuditCache() {
        ensureTableExists();
        ConcurrentHashMap<String, ConcurrentHashMap<String, AuditEntry>> auditCache = new ConcurrentHashMap<>();
        try {
            Connection connection = DatabaseManager.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM " + DATABASE_NAME);
            while (resultSet.next()) {
                DatabaseEntry entry = DatabaseEntry.fromResultSet(resultSet);
                if (!auditCache.containsKey(entry.guildId())) {
                    auditCache.put(entry.guildId(), new ConcurrentHashMap<>());
                }
                auditCache.get(entry.guildId()).put(entry.qid(), entry);
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        return auditCache;
    }

    public static Long getQidCounter() {
        ensureTableExists();
        try {
            Connection connection = DatabaseManager.getInstance().getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + DATABASE_NAME);

            long count = 0L;
            if (resultSet.next()) {
                count = Integer.toUnsignedLong(resultSet.getInt(1));
            }

            resultSet.close();
            statement.close();
            return count;
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

}
