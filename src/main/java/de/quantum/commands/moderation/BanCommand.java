package de.quantum.commands.moderation;

import de.quantum.core.commands.CommandAnnotation;
import de.quantum.core.commands.CommandInterface;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

@CommandAnnotation
public class BanCommand implements CommandInterface<SlashCommandInteractionEvent> {
    @Override
    public CommandDataImpl getCommandData() {
        return new CommandDataImpl("ban","Bans the provided user").addOptions();
    }

    @Override
    public void perform(SlashCommandInteractionEvent event) {

    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[]{Permission.BAN_MEMBERS};
    }
}
