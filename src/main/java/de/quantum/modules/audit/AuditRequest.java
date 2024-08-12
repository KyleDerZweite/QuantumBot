package de.quantum.modules.audit;

import de.quantum.core.utils.EmbedUtils;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.TargetType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.util.List;
import java.util.Objects;

@Getter
public class AuditRequest {

    public static final int MAX_ENTRIES_PER_EMBED = 5;

    private final String requestId;
    private final Guild guild;
    private final InteractionHook hook;

    @Setter
    private Member member;
    @Setter
    private IMentionable target;
    @Setter
    private Integer actionTypeId;
    @Setter
    private Integer targetTypeOrdinal;

    @Setter
    private List<LogEntry> filteredLogEntries;

    private int index = 0;

    public AuditRequest(Guild guild, InteractionHook hook) {
        this.requestId = AuditHandler.getInstance().getAuditRequestId();
        this.guild = Objects.requireNonNull(guild);
        this.hook = Objects.requireNonNull(hook);

        String memberId = member != null ? member.getId() : null;
        String targetId = target != null ? target.getId() : null;

        this.filteredLogEntries = AuditHandler.getInstance().getSimpleFilteredLogEntries(
                guild.getId(),
                memberId, targetId,
                actionTypeId, targetTypeOrdinal
        );

        if (!AuditHandler.getInstance().getActiveAuditRequests().containsKey(requestId)) {
            AuditHandler.getInstance().getActiveAuditRequests().put(requestId, this);
        }
    }

    public void next() {
        if (index + MAX_ENTRIES_PER_EMBED < filteredLogEntries.size()) {
            index += MAX_ENTRIES_PER_EMBED;
        }
        update();
    }

    public void previous() {
        if (index - MAX_ENTRIES_PER_EMBED > 0) {
            index -= MAX_ENTRIES_PER_EMBED;
        }
        update();
    }

    public int getMaxIndex() {
        return Math.min(filteredLogEntries.size(), index + MAX_ENTRIES_PER_EMBED);
    }

    public List<LogEntry> getTrimmedFilteredLogEntries() {
        return filteredLogEntries.subList(index, getMaxIndex());
    }

    private EmbedBuilder getAuditEmbedBuilder() {
        EmbedBuilder embedBuilder = EmbedUtils.getStandardEmbedBuilder();
        embedBuilder.setTitle("Audit-Logs (%s-%s/%s)".formatted(index, getMaxIndex(), filteredLogEntries.size()));

        String memberStr = member != null ? member.getAsMention() : "n/a";
        String targetStr = target != null ? target.getAsMention() : "n/a";
        String actionTypeIdStr = actionTypeId != null ? ActionType.from(actionTypeId).toString() : "n/a";
        String targetTypeOrdinalStr = targetTypeOrdinal != null ? TargetType.values()[targetTypeOrdinal].toString() : "n/a";

        embedBuilder.addField("Filters", """
                `Member      `: %s
                `Target      `: %s
                `Action Type `: %s
                `Target Type `: %s
                """.formatted(memberStr, targetStr, actionTypeIdStr, targetTypeOrdinalStr), false);

        for (LogEntry logEntry : getTrimmedFilteredLogEntries()) {
            embedBuilder.addField(logEntry.toString(), logEntry.getFieldValue(), false);
        }
        return embedBuilder;
    }

    private String getButtonKey(String name) {
        return "%s_%s_%s".formatted(AuditHandler.AUDIT_BUTTON_ID, requestId, name);
    }

    private Button[] getAuditButtons() {
        return new Button[]{
                Button.primary(getButtonKey("previous"), "Previous"),
                Button.secondary(getButtonKey("edit-filter"), "Change Filter"),
                Button.primary(getButtonKey("next"), "Next")
        };
    }

    private Button[] getDisabledAuditButtons() {
        return new Button[]{
                Button.primary(getButtonKey("previous"), "Previous").asDisabled(),
                Button.secondary(getButtonKey("edit-filter"), "Change Filter").asDisabled(),
                Button.primary(getButtonKey("next"), "Next").asDisabled()
        };
    }

    public void update() {
        update(false);
    }

    public void update(boolean disabled) {
        EmbedBuilder embedBuilder = getAuditEmbedBuilder();
        MessageEditBuilder msgBuilder = new MessageEditBuilder();
        msgBuilder.setEmbeds(embedBuilder.build());
        if (disabled) {
            msgBuilder.setActionRow(getDisabledAuditButtons());
        } else {
            msgBuilder.setActionRow(getAuditButtons());
        }
        hook.editOriginal(msgBuilder.build()).queue();
    }

    public void changeFilter() {

    }

    public void onTimeoutRemove() {
        update(true);
    }

}
