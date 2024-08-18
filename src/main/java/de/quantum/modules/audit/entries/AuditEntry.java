package de.quantum.modules.audit.entries;

import de.quantum.core.utils.StringUtils;
import net.dv8tion.jda.api.audit.ActionType;

public interface AuditEntry {

    String qid();

    String botId();

    String guildId();

    String memberMention();

    String memberId();

    String targetString();

    String targetId();

    int typeKey();

    String changeString();

    String reason();

    long epochSecond();

    default String getNameValue() {
        return "AuditEntry(%s)".formatted(qid());
    }

    default String getFieldValue() {
        return """
                `Member      `: %s (%s)
                `Target      `: %s (%s)
                `Action Type `: %s (%s)
                `Target Type `: %s (%s)
                `Change      `: %s
                `Reason      `: %s
                `Timestamp   `: <t:%s:R>
                """.formatted(
                memberMention(), memberId(),
                targetString(), targetId(),
                StringUtils.convertUpperCaseToTitleCase(ActionType.from(typeKey()).name()), ActionType.from(typeKey()).getKey(),
                StringUtils.convertUpperCaseToTitleCase(ActionType.from(typeKey()).getTargetType().name()), ActionType.from(typeKey()).getTargetType().ordinal(),
                changeString(),
                reason(), epochSecond()
        );
    }
}
