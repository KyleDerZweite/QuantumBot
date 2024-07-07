package de.quantum.modules.dummybots.commands;

import de.quantum.core.commands.CommandAnnotation;
import de.quantum.core.commands.CommandInterface;
import de.quantum.core.commands.CommandType;
import de.quantum.core.utils.CheckUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.Objects;

@CommandAnnotation
public class WriteCommand implements CommandInterface<SlashCommandInteractionEvent> {

    @Override
    public CommandDataImpl getCommandData() {
        return new CommandDataImpl("write", "Writes a message in the Channel").addOptions(
                new OptionData(OptionType.STRING, "message", "the message to write", true),
                new OptionData(OptionType.CHANNEL, "channel", "the channel to send in", false)
        );
    }

    @Override
    public void perform(SlashCommandInteractionEvent event) {
        MessageChannel channel = event.getChannel();
        if (CheckUtils.checkNotNull(event.getOption("channel")) && event.getChannelType() != ChannelType.PRIVATE) {
            channel = Objects.requireNonNull(event.getOption("channel")).getAsChannel().asGuildMessageChannel();
        }
        try {
            channel.sendMessage(Objects.requireNonNull(event.getOption("message")).getAsString()).queue();
            event.getHook().editOriginal("Sent Message").queue();
        } catch (Exception e) {
            event.getHook().editOriginal("Got Error: %s".formatted(e.getMessage())).queue();
        }
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
