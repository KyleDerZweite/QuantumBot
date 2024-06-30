package de.luxury.modules.speeddating;

import de.luxury.core.commands.CommandAnnotation;
import de.luxury.core.commands.CommandInterface;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

@CommandAnnotation
public class SpeedDatingCommand implements CommandInterface<SlashCommandInteractionEvent> {

    @Override
    public CommandDataImpl getCommandData() {
        CommandDataImpl commandData = new CommandDataImpl("","");

        return commandData;
    }

    @Override
    public void perform(SlashCommandInteractionEvent event) {

    }

    @Override
    public Permission[] getPermissions() {
        return CommandInterface.super.getPermissions();
    }
}
