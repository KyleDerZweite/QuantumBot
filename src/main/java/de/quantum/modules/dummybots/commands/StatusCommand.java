package de.quantum.modules.dummybots.commands;

import de.quantum.core.commands.CommandAnnotation;
import de.quantum.core.commands.CommandInterface;
import de.quantum.core.commands.CommandType;
import de.quantum.core.utils.StatusUtils;
import de.quantum.modules.dummybots.DummyBotManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.concurrent.TimeUnit;

@CommandAnnotation
public class StatusCommand implements CommandInterface<SlashCommandInteractionEvent> {

    @Override
    public CommandDataImpl getCommandData() {
        return new CommandDataImpl("status", "Shows Bot status");
    }

    @Override
    public void perform(SlashCommandInteractionEvent event) {
        if (!event.isFromGuild()) {
            event.getHook().editOriginal("This command does not work in DM-Chat").queue();
            return;
        }
        try {
            assert event.getGuild() != null;
            assert event.getMember() != null;
            StatusUtils statusUtils = DummyBotManager.getInstance().getStatusUtils();
            MessageCreateBuilder messageCreateBuilder = statusUtils.getMessageCreateBuilderForPage(event.getJDA(), event.getGuild(), event.getMember());
            event.getChannel().sendMessage(messageCreateBuilder.build()).queue(
                    message -> message.delete().queueAfter(10, TimeUnit.MINUTES)
            );
            event.getHook().editOriginal("Sent Status Embed, deleting after 10min").queue();
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
