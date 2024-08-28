package de.quantum.modules.reminder.commands;

import de.quantum.core.commands.CommandInterface;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class ReminderCommand implements CommandInterface<SlashCommandInteractionEvent> {

    @Override
    public CommandDataImpl getCommandData() {
        return null;
    }

    @Override
    public void perform(SlashCommandInteractionEvent event) {

    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[]{Permission.MESSAGE_MANAGE};
    }
}
