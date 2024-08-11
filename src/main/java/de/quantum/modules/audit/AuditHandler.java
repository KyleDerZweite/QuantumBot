package de.quantum.modules.audit;

import de.quantum.core.ShardMan;
import de.quantum.core.database.DatabaseManager;
import de.quantum.core.shutdown.ShutdownAnnotation;
import de.quantum.core.shutdown.ShutdownInterface;
import de.quantum.core.utils.CheckUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ShutdownAnnotation
public class AuditHandler implements ShutdownInterface {

    public static final String TABLE_NAME = "audit_entries";

    private static volatile AuditHandler INSTANCE = null;

    private Long qidLogCounter = null;

    /**
     * A concurrent cache for storing audit log entries.
     * The outer ConcurrentHashMap uses the guild ID (String) as the key,
     * and maps to another ConcurrentHashMap that contains the audit logs for that guild.
     * <p>
     * The inner ConcurrentHashMap uses the hex ID (String) as the key, which is the hexadecimal
     * representation of the quantum log ID, and maps to a LogEntry object.
     * <p>
     * This structure allows efficient storage and retrieval of audit logs by their guild and log ID.
     */
    @Getter
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, LogEntry>> guildAuditLogCache = new ConcurrentHashMap<>();

    private AuditHandler() {
        if (INSTANCE != null) {
            throw new AssertionError(
                    "Another instance of "
                            + AuditHandler.class.getName()
                            + " class already exists, Can't create a new instance.");
        }
    }

    public static AuditHandler getInstance() {
        if (INSTANCE == null) {
            synchronized (AuditHandler.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AuditHandler();
                }
            }
        }
        return INSTANCE;
    }

    public void getAllAuditLogs() {
        ResultSet rs = DatabaseManager.getInstance().selectFrom("*", "audit_log");
        try {
            while (rs.next()) {
                // pass
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }


    public void loadQidLogCounter() {
        qidLogCounter = 0L;
        //TODO load from db
    }

    public String getQidLogCounter() {
        if (CheckUtils.checkNull(qidLogCounter)) {
            loadQidLogCounter();
        }

        String hexLongId = Long.toHexString(qidLogCounter); // 16 characters wide for long
        qidLogCounter++;
        return hexLongId;
    }

    public List<LogEntry> getSimpleFilteredLogEntries(@NotNull String guildId, String memberId, String targetId, Integer actionTypeId, Integer targetTypeOrdinal) {
        return getSimpleFilteredLogEntries(guildId, memberId, targetId, actionTypeId, targetTypeOrdinal, 100);
    }

    public List<LogEntry> getSimpleFilteredLogEntries(@NotNull String guildId, String memberId, String targetId, Integer actionTypeId, Integer targetTypeOrdinal, int amount) {
        return getAuditLogCache(guildId).values().stream()
                .filter(entry -> {
                    if (memberId != null && !entry.getUserId().equals(memberId)) return false;
                    if (targetId != null && !entry.getTargetId().equals(targetId)) return false;
                    if (actionTypeId != null && entry.getTypeRaw() != actionTypeId) return false;
                    if (targetTypeOrdinal != null && entry.getTargetType().ordinal() != targetTypeOrdinal) return false;
                    return true;
                })
                .limit(amount)
                .toList();
    }

    public ConcurrentHashMap<String, LogEntry> getAuditLogCache(String guildId) {
        if (!guildAuditLogCache.containsKey(guildId)) {
            guildAuditLogCache.put(guildId, new ConcurrentHashMap<>());
        }
        return guildAuditLogCache.get(guildId);
    }

    public void cacheLog(Guild guild, LogEntry logEntry) {
        if (!guildAuditLogCache.containsKey(guild.getId())) {
            guildAuditLogCache.put(guild.getId(), new ConcurrentHashMap<>());
        }
        getAuditLogCache(guild.getId()).put(logEntry.getQid(), logEntry);
    }

    @Override
    public void shutdown() {
        guildAuditLogCache.forEach((guildId, auditLogCache) -> {
            auditLogCache.forEach((qid, logEntry) -> {
                // Output the log string (or export to a database)
                System.out.println(logEntry.toString());
                // Example of exporting to a database:
                // db.insertAuditLogEntry(shardId, timestamp, userId, userName, actionType.name(), targetId, targetName, reason, finalLogString);
            });
        });
    }
}
