package de.quantum.modules.dummybots.commands;

import de.quantum.core.commands.CommandAnnotation;
import de.quantum.core.commands.CommandInterface;
import de.quantum.core.commands.CommandType;
import de.quantum.core.utils.CheckUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.List;
import java.util.Objects;

@CommandAnnotation
public class DummyDeleteCommand implements CommandInterface<SlashCommandInteractionEvent> {

    private static final int DEFAULT_AMOUNT = 1;
    private static final int MAX_AMOUNT = 10;

    @Override
    public CommandDataImpl getCommandData() {
        return new CommandDataImpl("delete", "Deletes messages (up to 10)").addOptions(
                new OptionData(OptionType.INTEGER, "amount", "The Amount of messages", false),
                new OptionData(OptionType.CHANNEL, "channel", "The channel to delete in", false)
        );
    }

    @Override
    public void perform(SlashCommandInteractionEvent event) {
        try {
            int amount = DEFAULT_AMOUNT;
            if (CheckUtils.checkNotNull(event.getOption("amount"))) {
                amount = Objects.requireNonNull(event.getOption("amount")).getAsInt();
                if (amount > MAX_AMOUNT) {
                    amount = MAX_AMOUNT;
                }
            }
            TextChannel channel = Objects.requireNonNull(event.getChannel()).asTextChannel();
            if (CheckUtils.checkNotNull(event.getOption("channel")) && event.isFromGuild()) {
                channel = Objects.requireNonNull(event.getOption("channel")).getAsChannel().asTextChannel();
            }
            assert event.getGuild() != null;
            if (!event.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE)) {
                event.getHook().editOriginal("Got Error: Bot is missing Permission: MESSAGE_MANAGE").queue();
                return;
            }

            RestAction<List<Message>> messages = channel.getHistory().retrievePast(amount);
            TextChannel finalChannel = channel;
            messages.queue(msgList -> {
                // Bulk delete the messages
                finalChannel.deleteMessages(msgList).queue(
                        success -> event.getHook().editOriginal("Deleted " + msgList.size() + " messages.").queue(),
                        error -> event.getHook().editOriginal("Failed to delete messages. Make sure the messages are not older than 2 weeks.").queue()
                );
            });

        } catch (Exception e) {
            event.getHook().editOriginal("Got Error: %s".formatted(e)).queue();
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
