package de.quantum.modules.dummybots.commands;

import de.quantum.core.commands.CommandAnnotation;
import de.quantum.core.commands.CommandInterface;
import de.quantum.core.commands.CommandType;
import de.quantum.core.utils.StatusUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

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
            event.getHook().editOriginalEmbeds(StatusUtils.getStatusEmbed(event.getJDA(), event.getGuild(), event.getMember())).queue();
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
