package de.luxury.modules.commands;

import de.luxury.core.commands.CommandAnnotation;
import de.luxury.core.commands.CommandInterface;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

@CommandAnnotation
public class Ping implements CommandInterface<SlashCommandInteractionEvent> {

    @Override
    public CommandDataImpl getCommandData() {
        return new CommandDataImpl("ping", "Ping - Pong Command (ping in ms)");
    }

    @Override
    public void perform(SlashCommandInteractionEvent event) {
        long time = System.currentTimeMillis();
        event.getHook().editOriginal("Pong! `(%d ms)`".formatted(System.currentTimeMillis() - time)).queue();
    }

    @Override
    public Permission[] getPermissions() {
        return CommandInterface.super.getPermissions();
    }
}
