package de.quantum.modules.audit;

import de.quantum.core.commands.CommandAnnotation;
import de.quantum.core.commands.CommandInterface;
import de.quantum.core.commands.CommandType;
import de.quantum.core.module.ModuleCommand;
import de.quantum.core.utils.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.TargetType;
import net.dv8tion.jda.api.entities.IMentionable;
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
@ModuleCommand(
        moduleName = "Audit"
)
public class AuditCommand implements CommandInterface<SlashCommandInteractionEvent> {
    @Override
    public CommandDataImpl getCommandData() {
        return new CommandDataImpl("audit", "Shows the audit entries for specific filters").addOptions(
                new OptionData(OptionType.USER, "member", "The moderating member", false),
                new OptionData(OptionType.MENTIONABLE, "target", "The targeted member", false),
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
        IMentionable target = event.getOption("target") != null ? Objects.requireNonNull(event.getOption("target")).getAsMentionable() : null;
        Integer actionTypeId = event.getOption("action_type") != null ? Objects.requireNonNull(event.getOption("action_type")).getAsInt() : null;
        Integer targetTypeOrdinal = event.getOption("target_type") != null ? Objects.requireNonNull(event.getOption("target_type")).getAsInt() : null;

        AuditRequest auditRequest = new AuditRequest(event.getGuild(), event.getHook());
        auditRequest.setMember(member);
        auditRequest.setTarget(target);
        auditRequest.setActionTypeId(actionTypeId);
        auditRequest.setTargetTypeOrdinal(targetTypeOrdinal);

        auditRequest.update();
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
