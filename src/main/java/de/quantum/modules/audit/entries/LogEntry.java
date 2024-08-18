package de.quantum.modules.audit.entries;

import de.quantum.modules.audit.AuditManager;
import net.dv8tion.jda.api.audit.AuditLogChange;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record LogEntry(String qid, AuditLogEntry auditLogEntry, long epochSecond) implements AuditEntry {

    public LogEntry(AuditLogEntry auditLogEntry) {
        this(AuditManager.getInstance().getQidLogCounter(), auditLogEntry, auditLogEntry.getTimeCreated().toEpochSecond());
    }

    @Override
    public String botId() {
        return auditLogEntry.getJDA().getSelfUser().getId();
    }

    @Override
    public String guildId() {
        return auditLogEntry.getGuild().getId();
    }

    @Nullable
    public Member getMember() {
        return auditLogEntry.getGuild().getMemberById(memberId());
    }

    @Override
    public String memberMention() {
        return getMember() != null ? getMember().getAsMention() : "";
    }

    @Override
    public String memberId() {
        return auditLogEntry.getUserId();
    }

    @Override
    public String targetString() {
        return AuditManager.getInstance().getTargetString(auditLogEntry.getGuild(), auditLogEntry.getType().getTargetType(), targetId());
    }

    @Override
    @NotNull
    public String targetId() {
        return auditLogEntry.getTargetId();
    }

    @Override
    @Nullable
    public String reason() {
        return auditLogEntry.getReason();
    }

    @Override
    public int typeKey() {
        return auditLogEntry.getType().getKey();
    }

    @Override
    public String changeString() {
        // Handle changes (if any)
        Map<String, AuditLogChange> changeMap = auditLogEntry.getChanges();
        StringBuilder changes = new StringBuilder();
        if (!changeMap.isEmpty()) {
            for (AuditLogChange change : changeMap.values()) {
                String key = change.getKey().replace("$", "");
                Object newValue = change.getNewValue();
                changes.append(key).append(": ").append(newValue != null ? newValue : "None");
            }
        }
        return changes.toString();
    }
}
