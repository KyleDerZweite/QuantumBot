package de.quantum.modules.audit.commands;

import de.quantum.core.commands.CommandAnnotation;
import de.quantum.core.commands.CommandInterface;
import de.quantum.core.commands.CommandType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class AuditCommand implements CommandInterface<SlashCommandInteractionEvent> {
    @Override
    public CommandDataImpl getCommandData() {
        return null;
    }

    @Override
    public void perform(SlashCommandInteractionEvent event) {
        //
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
