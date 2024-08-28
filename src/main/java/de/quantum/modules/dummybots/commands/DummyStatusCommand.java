package de.quantum.modules.dummybots.commands;

import de.quantum.commands.misc.StatusCommand;
import de.quantum.core.commands.CommandAnnotation;
import de.quantum.core.commands.CommandInterface;
import de.quantum.core.commands.CommandType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

@CommandAnnotation
public class DummyStatusCommand implements CommandInterface<SlashCommandInteractionEvent> {

    @Override
    public CommandDataImpl getCommandData() {
        return new CommandDataImpl("status", "Shows Bot status");
    }

    @Override
    public void perform(SlashCommandInteractionEvent event) {
        new StatusCommand().perform(event);
    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[]{Permission.MESSAGE_MANAGE};
    }

    @Override
    public CommandType getType() {
        return CommandType.DUMMY;
    }

}
