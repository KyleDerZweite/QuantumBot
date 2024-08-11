package de.quantum.modules.audit;

import de.quantum.core.commands.CommandAnnotation;
import de.quantum.core.commands.CommandInterface;
import de.quantum.core.commands.CommandType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.TargetType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@CommandAnnotation
public class AuditCommand implements CommandInterface<SlashCommandInteractionEvent> {
    @Override
    public CommandDataImpl getCommandData() {
        return new CommandDataImpl("audit", "Shows the audit entries for specific filters").addOptions(
                new OptionData(OptionType.USER, "member", "The moderating member", false),
                new OptionData(OptionType.USER, "target", "The targeted member", false),
                new OptionData(OptionType.INTEGER, "action_type", "The actionType ID of the audit entry", false),
                new OptionData(OptionType.INTEGER, "target_type", "The targetType of the audit entry", false).addChoices(
                        Arrays.stream(TargetType.values())
                                .map(targetType -> new Command.Choice(targetType.toString(), targetType.ordinal()))
                                .toArray(Command.Choice[]::new)
                ),
                new OptionData(OptionType.BOOLEAN, "advanced", "Switches to the advanced search options", false)
        );
    }

    @Override
    public void perform(SlashCommandInteractionEvent event) {
        if (event.getOption("advanced") != null && Objects.requireNonNull(event.getOption("advanced")).getAsBoolean()) {
            event.getHook().editOriginal("Advanced to be implemented").queue();
            return;
        }

        Member member = event.getOption("member") != null ? Objects.requireNonNull(event.getOption("member")).getAsMember() : null;
        Member target = event.getOption("target") != null ? Objects.requireNonNull(event.getOption("target")).getAsMember() : null;
        Integer actionTypeId = event.getOption("actionType") != null ? Objects.requireNonNull(event.getOption("actionType")).getAsInt() : null;
        Integer targetTypeOrdinal = event.getOption("targetType") != null ? Objects.requireNonNull(event.getOption("targetType")).getAsInt() : null;

        String memberId = member != null ? member.getId() : null;
        String targetId = target != null ? target.getId() : null;

        System.out.println(memberId);
        System.out.println(targetId);
        System.out.println(actionTypeId);
        System.out.println(targetTypeOrdinal);

        List<LogEntry> filteredLogEntries = AuditHandler.getInstance().getSimpleFilteredLogEntries(
                Objects.requireNonNull(event.getGuild()).getId(),
                memberId, targetId,
                actionTypeId, targetTypeOrdinal
        );

        event.getHook().editOriginal(Arrays.toString(filteredLogEntries.toArray())).queue();
    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[]{Permission.VIEW_AUDIT_LOGS};
    }

    @Override
    public CommandType getType() {
        return CommandType.GUILD_ONLY;
    }
}
