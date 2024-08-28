package de.quantum.modules.dummybots;

import de.quantum.core.commands.CommandAnnotation;
import de.quantum.core.commands.CommandInterface;
import de.quantum.core.commands.CommandType;
import de.quantum.core.module.ModuleCommand;
import de.quantum.core.utils.AudioManagerUtils;
import de.quantum.core.utils.CheckUtils;
import de.quantum.core.utils.Secret;
import de.quantum.core.utils.StatusUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@CommandAnnotation
@ModuleCommand(moduleName = "DummyBot")
public class DummyBotCommand implements CommandInterface<SlashCommandInteractionEvent> {

    @Override
    public CommandDataImpl getCommandData() {
        return new CommandDataImpl("dummy", "A command to manage the dummy bots in a group")
                .addSubcommands(
                        new SubcommandData("add", "Adds a new dummy bot")
                                .addOption(OptionType.STRING, "token", "The Token of the dummy bot!", true),
                        new SubcommandData("start", "Starts the dummy bot")
                                .addOption(OptionType.USER, "bot_user", "The bot to start"),
                        new SubcommandData("stop", "Stops the dummy bot")
                                .addOption(OptionType.USER, "bot_user", "The bot to stop"),
                        new SubcommandData("status", "The Status of the current dummies"),
                        new SubcommandData("write", "All dummies writes the provided message in the channel")
                                .addOptions(
                                        new OptionData(OptionType.STRING, "message", "the message to write", true),
                                        new OptionData(OptionType.CHANNEL, "channel", "the channel to send in", false)),
                        new SubcommandData("delete", "Deletes 1 message to test permission")
                                .addOptions(new OptionData(OptionType.CHANNEL, "channel", "The channel to delete in", false)))
                .addSubcommandGroups(
                        new SubcommandGroupData("voice", "All dummies joins / leaves a voice-channel")
                                .addSubcommands(
                                        new SubcommandData("join", "Joins a voice channel")
                                                .addOption(OptionType.CHANNEL, "voice_channel", "The voice channel to join", false),
                                        new SubcommandData("leave", "Leaves a voice channel")
                                )
                );
    }

    @Override
    public void perform(SlashCommandInteractionEvent event) {
        assert event.getGuild() != null;
        ArrayList<JDA> dummies = DummyBotManager.getInstance().getGuildDummyJdaInstances(event.getGuild().getId());
        if (dummies.isEmpty()) {
            event.getHook().editOriginal("There are no dummy bots in your guild. Please contact a bot admin!").queue();
            return;
        }
        String commandStatement = "";
        if (CheckUtils.checkNotNull(event.getSubcommandGroup())) {
            commandStatement += event.getSubcommandGroup() + "_";
        }
        commandStatement += event.getSubcommandName();
        switch (commandStatement) {
            case "add" -> handleAdd(event);
            case "start" -> handleStart(event);
            case "stop" -> handleStop(event);
            case "status" -> handleStatus(event);
            case "write" -> handleWrite(event);
            case "delete" -> handleDelete(event);
            case "voice_join" -> handleVoiceJoin(event);
            case "voice_leave" -> handleVoiceLeave(event);
            default -> event.getHook().editOriginal("SubCommand not found").queue();
        }
    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[]{Permission.MANAGE_SERVER};
    }

    @Override
    public CommandType getType() {
        return CommandType.GUILD_ONLY;
    }

    public void handleAdd(SlashCommandInteractionEvent event) {
        if (event.getOption("token") == null) {
            event.getHook().editOriginal("Please provide a valid token!").queue();
            return;
        }
        String token = Objects.requireNonNull(event.getOption("token")).getAsString();
        String encryptedToken = Secret.encrypt(token);
        try {
            JDA dummyJda = DummyBotManager.getInstance().getTestDummyJDA(encryptedToken);
            String botId = dummyJda.getSelfUser().getId();
            String guildId = Objects.requireNonNull(event.getGuild()).getId();
            DummyBotDatabaseManager.addDummyBot(botId, encryptedToken, guildId);
            dummyJda.shutdown();
            dummyJda.shutdownNow();
            DummyBotManager.getInstance().startBotId(guildId, botId);
        } catch (InvalidTokenException ignored) {
            event.getHook().editOriginal("Please provide a valid token!").queue();
        }
    }

    public void handleStart(SlashCommandInteractionEvent event) {
        if (event.getOption("bot_user") == null) {
            DummyBotManager.getInstance().startBotsFromGuild(Objects.requireNonNull(event.getGuild()).getId());
        } else {
            Member member = Objects.requireNonNull(event.getOption("bot_user")).getAsMember();
            assert member != null;
            String botId = member.getId();
            DummyBotManager.getInstance().startBotId(Objects.requireNonNull(event.getGuild()).getId(), botId);
        }
        event.getHook().editOriginal("Successfully started.").queue();
    }

    public void handleStop(SlashCommandInteractionEvent event) {
        if (event.getOption("bot_user") == null) {
            DummyBotManager.getInstance().stopBotsFromGuild(Objects.requireNonNull(event.getGuild()).getId());
        } else {
            Member member = Objects.requireNonNull(event.getOption("bot_user")).getAsMember();
            assert member != null;
            String botId = member.getId();
            DummyBotManager.getInstance().stopBotId(Objects.requireNonNull(event.getGuild()).getId(), botId);
        }
        event.getHook().editOriginal("Successfully stopped.").queue();
    }

    public void handleStatus(SlashCommandInteractionEvent event) {
        assert event.getGuild() != null;
        assert event.getMember() != null;
        DummyBotManager.getInstance().getGuildDummyJdaInstances(event.getGuild().getId()).forEach((jda) -> {
            Guild guild = jda.getGuildById(event.getGuild().getId());
            assert guild != null;
            StatusUtils statusUtils = DummyBotManager.getInstance().getStatusUtils();
            MessageCreateBuilder messageCreateBuilder = statusUtils.getMessageCreateBuilderForPage(jda, guild, event.getMember());
            event.getChannel().sendMessage(messageCreateBuilder.build()).queue(
                    message -> message.delete().queueAfter(10, TimeUnit.MINUTES)
            );
        });

        event.getHook().editOriginal("Sent Message Embeds, auto-deletes after 10min!").queue();
    }

    public void handleWrite(SlashCommandInteractionEvent event) {
        assert event.getGuild() != null;
        String channelId = event.getChannelId();
        if (CheckUtils.checkNotNull(event.getOption("channel"))) {
            channelId = Objects.requireNonNull(event.getOption("channel")).getAsChannel().getId();
        }
        try {
            String finalChannelId = channelId;
            DummyBotManager.getInstance().getGuildDummyJdaInstances(event.getGuild().getId()).forEach((jda) -> {
                Guild guild = jda.getGuildById(event.getGuild().getId());
                assert guild != null;
                assert finalChannelId != null;
                MessageChannel channel = guild.getTextChannelById(finalChannelId);
                assert channel != null;
                channel.sendMessage(Objects.requireNonNull(event.getOption("message")).getAsString()).queue(
                        message -> message.delete().queueAfter(1L, TimeUnit.MINUTES));
            });
            event.getHook().editOriginal("Sent Message").queue();
        } catch (Exception e) {
            event.getHook().editOriginal("Got Error: %s".formatted(e.getMessage())).queue();
        }


    }

    public void handleDelete(SlashCommandInteractionEvent event) {
        assert event.getGuild() != null;
        String channelId = event.getChannelId();
        if (CheckUtils.checkNotNull(event.getOption("channel"))) {
            channelId = Objects.requireNonNull(event.getOption("channel")).getAsChannel().getId();
        }

        try {
            StringBuilder endMessageString = new StringBuilder();
            endMessageString.append("Dummies Delete-Result:\n");
            String finalChannelId = channelId;
            DummyBotManager.getInstance().getGuildDummyJdaInstances(event.getGuild().getId()).forEach((jda) -> {
                assert finalChannelId != null;
                Guild guild = jda.getGuildById(event.getGuild().getId());
                assert guild != null;
                TextChannel channel = guild.getTextChannelById(finalChannelId);
                assert channel != null;
                if (!guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_MANAGE)) {
                    endMessageString.append("> Error    : ").append(guild.getSelfMember().getAsMention())
                            .append(" - Missing **Permission.MESSAGE_MANAGE**").append("\n");
                } else {
                    Objects.requireNonNull(event.getGuild().getTextChannelById(finalChannelId)).sendMessage("TEST-MESSAGE").queue();
                    RestAction<List<Message>> messages = channel.getHistory().retrievePast(1);
                    messages.queue(msgList -> {
                        // Bulk delete the messages
                        channel.deleteMessages(msgList).queue(
                                success -> event.getHook().editOriginal("Deleted " + msgList.size() + " messages.").queue(),
                                error -> event.getHook().editOriginal("Failed to delete messages. Make sure the messages are not older than 2 weeks.").queue()
                        );
                    });
                    endMessageString.append("> Success  : ").append(guild.getSelfMember().getAsMention()).append("\n");
                }
                event.getHook().editOriginal(endMessageString.toString()).queue();
            });
        } catch (Exception e) {
            event.getHook().editOriginal("Got Error: %s".formatted(e.getMessage())).queue();
        }
    }

    public void handleVoiceJoin(SlashCommandInteractionEvent event) {
        assert event.getMember() != null;
        assert event.getGuild() != null;
        VoiceChannel voiceChannel;
        if (CheckUtils.checkNotNull(event.getOption("voice_channel"))) {
            voiceChannel = Objects.requireNonNull(event.getOption("voice_channel")).getAsChannel().asVoiceChannel();
        } else if (CheckUtils.checkNotNull(event.getMember().getVoiceState())) {
            voiceChannel = Objects.requireNonNull(event.getMember().getVoiceState().getChannel()).asVoiceChannel();
        } else {
            event.getHook().editOriginal("No Voice Channel provided!").queue();
            return;
        }
        String finalChannelId = voiceChannel.getId();
        DummyBotManager.getInstance().getGuildDummyJdaInstances(event.getGuild().getId()).forEach((jda) -> {
            AudioManagerUtils.joinVoiceChannel(jda, event.getGuild().getId(), finalChannelId);
        });
        event.getHook().editOriginal("Dummies joined: %s".formatted(voiceChannel.getAsMention())).queue();
    }

    public void handleVoiceLeave(SlashCommandInteractionEvent event) {
        assert event.getGuild() != null;
        DummyBotManager.getInstance().getGuildDummyJdaInstances(event.getGuild().getId()).forEach((jda) -> {
            AudioManagerUtils.leaveVoiceChannel(jda, event.getGuild().getId());
        });
        event.getHook().editOriginal("Left Voice-Channels").queue();
    }

}
