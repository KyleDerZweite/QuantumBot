package de.quantum.modules.audit;

import net.dv8tion.jda.api.audit.ActionType;

public record AuditRecord(long qid, String userId, String targetId, int rawActionType, String action) {
}
