package de.quantum.modules.audit;

import de.quantum.core.utils.CheckUtils;
import de.quantum.core.utils.EmbedUtils;
import de.quantum.core.utils.SearchHandler;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.TargetType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.util.List;
import java.util.Objects;

@Getter
public class AuditRequest {

    public static final int MAX_ENTRIES_PER_EMBED = 4;

    private static final String MEMBER_INPUT_KEY = "member-input";
    private static final String TARGET_INPUT_KEY = "target-input";
    private static final String ACTION_TYPE_KEY = "action-type-input";
    private static final String TARGET_TYPE_KEY = "target-type-input";
    private static final String KEYWORD_KEY = "keyword-input";

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
    private String keyword = null;

    @Setter
    private List<LogEntry> filteredLogEntries;

    private int index = 0;

    public AuditRequest(Guild guild, InteractionHook hook) {
        this.requestId = AuditHandler.getInstance().getAuditRequestId();
        this.guild = Objects.requireNonNull(guild);
        this.hook = Objects.requireNonNull(hook);

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

    public int getMinIndex() {
        return Math.min(filteredLogEntries.size(), index + 1);
    }

    public List<LogEntry> getTrimmedFilteredLogEntries() {
        return filteredLogEntries.subList(index, getMaxIndex());
    }

    private EmbedBuilder getAuditEmbedBuilder() {
        EmbedBuilder embedBuilder = EmbedUtils.getStandardEmbedBuilder();
        embedBuilder.setTitle("Audit-Logs (%s-%s/%s)".formatted(getMinIndex(), getMaxIndex(), filteredLogEntries.size()));
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
        if (hook.isExpired()) return;
        String memberId = member != null ? member.getId() : null;
        String targetId = target != null ? target.getId() : null;

        this.filteredLogEntries = AuditHandler.getInstance().getFilteredLogEntries(
                guild.getId(),
                memberId, targetId,
                actionTypeId, targetTypeOrdinal
        );

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

    public void changeFilter(String key, String value) {
        switch (key) {
            case MEMBER_INPUT_KEY -> {
                if (CheckUtils.isDigit(value)) {
                    this.member = guild.getMemberById(value);
                } else {
                    this.member = SearchHandler.getClosestMatchingMemberByName(guild, value);
                }
            }
            case TARGET_INPUT_KEY -> {
                if (CheckUtils.isDigit(value)) {
                    this.target = AuditHandler.getInstance().getTarget(guild, targetTypeOrdinal, value);
                } else {
                    this.target = SearchHandler.getClosestMatchingIMentionableByName(guild, value);
                }
            }
            case ACTION_TYPE_KEY -> {
                if (CheckUtils.isDigit(value)) {
                    this.actionTypeId = Integer.valueOf(value);
                } else {
                    this.actionTypeId = null;
                }
            }
            case TARGET_TYPE_KEY -> {
                if (CheckUtils.isDigit(value)) {
                    int intValue = Integer.parseInt(value);
                    if (intValue > 0 && intValue <= TargetType.values().length) {
                        this.targetTypeOrdinal = intValue;
                    }
                } else {
                    this.actionTypeId = null;
                }
            }
            case KEYWORD_KEY -> this.keyword = value;
            default -> {
            }
        }
        update();
    }

    public void onTimeoutRemove() {
        update(true);
    }

    public Modal getChangeFilterModal() {
        return Modal.create(getButtonKey("modal"), "Change Filter")
                .addComponents(
                        ActionRow.of(TextInput.create(MEMBER_INPUT_KEY, "Member", TextInputStyle.SHORT)
                                .setPlaceholder("Enter member (name or ID)")
                                .setValue(member != null ? member.getId() : null)
                                .setRequired(false)
                                .build()
                        ),
                        ActionRow.of(TextInput.create(TARGET_INPUT_KEY, "Target", TextInputStyle.SHORT)
                                .setPlaceholder("Enter target (name or ID)")
                                .setValue(target != null ? target.getId() : null)
                                .setRequired(false)
                                .build()
                        ),
                        ActionRow.of(TextInput.create(ACTION_TYPE_KEY, "Action Type ID", TextInputStyle.SHORT)
                                .setPlaceholder("Enter action type ID")
                                .setValue(actionTypeId != null ? String.valueOf(actionTypeId) : null)
                                .setRequired(false)
                                .build()
                        ),
                        ActionRow.of(TextInput.create(TARGET_TYPE_KEY, "Target Type ID", TextInputStyle.SHORT)
                                .setPlaceholder("Enter action type ID")
                                .setValue(targetTypeOrdinal != null ? String.valueOf(targetTypeOrdinal) : null)
                                .setRequired(false)
                                .build()),
                        ActionRow.of(TextInput.create(KEYWORD_KEY, "Keyword Filter", TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Enter keyword filter (optional) split keywords with ','")
                                .setRequired(false)
                                .build()
                        )
                )
                .build();
    }
}
