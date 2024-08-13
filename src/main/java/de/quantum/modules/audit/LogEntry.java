package de.quantum.modules.audit;

import de.quantum.core.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.audit.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
@AllArgsConstructor
public class LogEntry {

    private final String qid;
    private final AuditLogEntry auditLogEntry;
    private final long epochSecond;

    public LogEntry(AuditLogEntry auditLogEntry) {
        this.qid = AuditHandler.getInstance().getQidLogCounter();
        this.auditLogEntry = auditLogEntry;
        this.epochSecond = auditLogEntry.getTimeCreated().toEpochSecond();
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
    public Member getMember() {
        return getGuild().getMemberById(getUserId());
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

    public String getFieldValue() {
        // Handle changes (if any)
        Map<String, AuditLogChange> changeMap = getChanges();
        StringBuilder changes = new StringBuilder();
        if (!changeMap.isEmpty()) {
            for (AuditLogChange change : changeMap.values()) {
                String key = change.getKey().replace("$", "");
                Object newValue = change.getNewValue();
                changes.append(key).append(": ")
                        .append(newValue != null ? newValue : "None");
            }
        }

        return """
                `Member      `: %s (%s)
                `Target      `: %s (%s)
                `Action Type `: %s (%s)
                `Target Type `: %s (%s)
                `Change      `: %s
                `Reason      `: %s
                `Timestamp   `: <t:%s:R>
                """.formatted(
                Objects.requireNonNull(getMember()).getAsMention(), getUserId(),
                AuditHandler.getInstance().getTargetString(getGuild(), getTargetType(), getTargetId()), getTargetId(),
                StringUtils.convertUpperCaseToTitleCase(getType().name()), getType().getKey(),
                StringUtils.convertUpperCaseToTitleCase(getType().getTargetType().name()), getType().getTargetType().ordinal(),
                changes,
                getReason() != null ? getReason() : "n/a",
                epochSecond
        );
    }

    @Override
    public String toString() {
        return "LogEntry(qid=%s, id=%s)".formatted(qid, auditLogEntry.getId());
    }
}
