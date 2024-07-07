package de.quantum.modules.dummybots.commands;

import de.quantum.core.commands.CommandAnnotation;
import de.quantum.core.commands.CommandInterface;
import de.quantum.core.commands.CommandType;
import de.quantum.core.utils.AudioManagerUtils;
import de.quantum.core.utils.CheckUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.Objects;

@CommandAnnotation
public class VoiceCommand implements CommandInterface<SlashCommandInteractionEvent> {

    @Override
    public CommandDataImpl getCommandData() {
        return new CommandDataImpl("voice", "Joins / Leaves a voice channel").addSubcommands(
                new SubcommandData("join", "Joins a voice channel")
                        .addOption(OptionType.CHANNEL, "voice_channel", "The voice channel to join", false),
                new SubcommandData("leave", "Leaves a voice channel")
        );
    }

    @Override
    public void perform(SlashCommandInteractionEvent event) {
        if (!event.isFromGuild()) {
            event.getHook().editOriginal("This command does not work in DM-Chat").queue();
            return;
        }
        if (CheckUtils.checkNull(event.getSubcommandName())) {
            event.getHook().editOriginal("SubCommand not found").queue();
            return;
        }

        try {
            assert event.getMember() != null;
            assert event.getGuild() != null;
            switch (event.getSubcommandName()) {
                case "join" -> {
                    VoiceChannel voiceChannel;
                    if (CheckUtils.checkNotNull(event.getOption("voice_channel"))) {
                        voiceChannel = Objects.requireNonNull(event.getOption("voice_channel")).getAsChannel().asVoiceChannel();
                    } else if (CheckUtils.checkNotNull(event.getMember().getVoiceState())) {
                        voiceChannel = Objects.requireNonNull(event.getMember().getVoiceState().getChannel()).asVoiceChannel();
                    } else {
                        event.getHook().editOriginal("No Voice Channel provided!").queue();
                        return;
                    }
                    AudioManagerUtils.joinVoiceChannel(event.getJDA(), event.getGuild().getId(), voiceChannel.getId());
                    event.getHook().editOriginal("Joined: %s".formatted(voiceChannel.getAsMention())).queue();
                }
                case "leave" -> {
                    AudioManagerUtils.leaveVoiceChannel(event.getJDA(), event.getGuild().getId());
                    event.getHook().editOriginal("Left Voice-Channels").queue();
                }
                default -> event.getHook().editOriginal("SubCommand not found").queue();
            }
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
