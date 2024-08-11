package de.quantum.modules.audit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audit.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class LogEntry {

    private final String qid;
    private final AuditLogEntry auditLogEntry;
    private final long epochSecond;

    public LogEntry(AuditLogEntry auditLogEntry) {
        this.qid = AuditHandler.getInstance().getQidLogCounter();
        this.auditLogEntry = auditLogEntry;
        this.epochSecond = System.currentTimeMillis();
    }

    public LogEntry(String qid, AuditLogEntry auditLogEntry) {
        this.qid = qid;
        this.auditLogEntry = auditLogEntry;
        this.epochSecond = auditLogEntry.getTimeCreated().toEpochSecond();
    }

    @NotNull
    public String getTargetId() {
        return auditLogEntry.getTargetId();
    }

    @Nullable
    public Webhook getWebhook() {
        return auditLogEntry.getWebhook();
    }

    @NotNull
    public Guild getGuild() {
        return auditLogEntry.getGuild();
    }

    @NotNull
    public String getUserId() {
        return auditLogEntry.getUserId();
    }

    @Nullable
    public User getUser() {
        return auditLogEntry.getUser();
    }

    @Nullable
    public String getReason() {
        return auditLogEntry.getReason();
    }

    @NotNull
    public JDA getJDA() {
        return auditLogEntry.getJDA();
    }

    @NotNull
    public Map<String, AuditLogChange> getChanges() {
        return auditLogEntry.getChanges();
    }

    @Nullable
    public AuditLogChange getChangeByKey(@Nullable AuditLogKey key) {
        return auditLogEntry.getChangeByKey(key);
    }

    @Nullable
    public AuditLogChange getChangeByKey(@Nullable String key) {
        return auditLogEntry.getChangeByKey(key);
    }

    @NotNull
    public List<AuditLogChange> getChangesForKeys(@NotNull AuditLogKey... keys) {
        return auditLogEntry.getChangesForKeys(keys);
    }

    @NotNull
    public Map<String, Object> getOptions() {
        return auditLogEntry.getOptions();
    }

    @Nullable
    public <T> T getOptionByName(@Nullable String name) {
        return auditLogEntry.getOptionByName(name);
    }

    @Nullable
    public <T> T getOption(@NotNull AuditLogOption option) {
        return auditLogEntry.getOption(option);
    }

    @NotNull
    public List<Object> getOptions(@NotNull AuditLogOption... options) {
        return auditLogEntry.getOptions(options);
    }

    @NotNull
    public ActionType getType() {
        return auditLogEntry.getType();
    }

    public int getTypeRaw() {
        return auditLogEntry.getTypeRaw();
    }

    @NotNull
    public TargetType getTargetType() {
        return auditLogEntry.getTargetType();
    }

    @Override
    public String toString() {
        int shardId = getJDA().getShardInfo().getShardId();
        String userId = getUserId();
        String targetId = getTargetId();
        ActionType actionType = getType(); // Human-readable action type
        String reason = getReason();

        // Convert epochMillis to a readable date/time string
        String timestamp = Instant.ofEpochSecond(getEpochSecond())
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Start constructing the log entry string
        StringBuilder logString = new StringBuilder();
        logString.append("[").append(timestamp).append("] ")
                .append("Shard ID: ").append(shardId).append(" | ")
                .append(actionType.name()).append(" ").append(" | ")
                .append("User: ").append(userId).append(" | ")
                .append("Target: ").append(targetId);

        // Append reason if available
        if (reason != null && !reason.isEmpty()) {
            logString.append(" | Reason: ").append(reason);
        }

        // Handle changes (if any)
        Map<String, AuditLogChange> changeMap = getChanges();
        if (!changeMap.isEmpty()) {
            logString.append(" | Changes: ");
            for (AuditLogChange change : changeMap.values()) {
                String key = change.getKey();
                Object oldValue = change.getOldValue();
                Object newValue = change.getNewValue();
                logString.append("[").append(key).append(": ")
                        .append(oldValue != null ? oldValue : "[None]")
                        .append(" -> ")
                        .append(newValue != null ? newValue : "None")
                        .append("] ");
            }
        }

        // Final log entry string
        return logString.toString();
    }
}
