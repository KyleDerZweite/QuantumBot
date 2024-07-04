package de.quantum.commands;

import de.quantum.core.LanguageManager;
import de.quantum.core.ShardMan;
import de.quantum.core.commands.CommandAnnotation;
import de.quantum.core.commands.CommandInterface;
import de.quantum.core.commands.CommandManager;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.Objects;

@CommandAnnotation
public class Ping implements CommandInterface<SlashCommandInteractionEvent> {

    @Override
    public CommandDataImpl getCommandData() {
        CommandDataImpl commandData = new CommandDataImpl("ping", LanguageManager.getString("ping_command_desc"));
        CommandManager.localizeCommandDescription(commandData, "ping_command_desc");
        return commandData;
    }

    @Override
    public void perform(SlashCommandInteractionEvent event) {
        double averageGatewayPing = Objects.requireNonNull(ShardMan.getInstance().getShardManager()).getAverageGatewayPing();
        int gatewayPing = (int) averageGatewayPing;
        event.getHook().editOriginal("Pong! `(%d ms)`".formatted(gatewayPing)).queue();
    }


}
