package de.quantum.commands;

import de.quantum.core.ShardMan;
import de.quantum.core.commands.CommandAnnotation;
import de.quantum.core.commands.CommandInterface;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.Objects;

@CommandAnnotation
public class Ping implements CommandInterface<SlashCommandInteractionEvent> {

    @Override
    public CommandDataImpl getCommandData() {
        return new CommandDataImpl("ping", "Ping - Pong Command (ping in ms)");
    }

    @Override
    public void perform(SlashCommandInteractionEvent event) {
        double averageGatewayPing = Objects.requireNonNull(ShardMan.getInstance().getShardManager()).getAverageGatewayPing();
        event.getHook().editOriginal("Pong! `(%fl ms)`".formatted(averageGatewayPing)).queue();
    }
}
