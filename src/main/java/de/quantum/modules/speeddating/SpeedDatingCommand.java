package de.quantum.modules.speeddating;

import de.quantum.core.commands.CommandAnnotation;
import de.quantum.core.commands.CommandInterface;
import de.quantum.core.commands.CommandType;
import de.quantum.core.module.ModuleCommand;
import de.quantum.modules.speeddating.entities.SpeedDatingConfig;
import de.quantum.modules.speeddating.entities.SpeedDatingEvent;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.Objects;

@Slf4j
@CommandAnnotation
@ModuleCommand(moduleName = "SpeedDating")
public class SpeedDatingCommand implements CommandInterface<SlashCommandInteractionEvent> {

    @Override
    public CommandDataImpl getCommandData() {
        return new CommandDataImpl("speed_dating", "Starts / Stops the speed-dating event").addSubcommands(
                new SubcommandData("start", "Starts the speed-dating event"),
                new SubcommandData("stop", "Stops the speed-dating event"),
                new SubcommandData("setup", "Setups the speed-dating event")
                        .addOptions(
                                new OptionData(OptionType.CHANNEL, "category", "The Category for the system", true),
                                new OptionData(OptionType.CHANNEL, "voice_channel", "The Channel where the users that want to participate join", true)
                        )
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS));
    }

    @Override
    public void perform(SlashCommandInteractionEvent event) {
        switch (Objects.requireNonNull(event.getSubcommandName())) {
            case "start" -> startSpeedDating(event);
            case "stop" -> stopSpeedDating(event);
            case "setup" -> setupSpeedDating(event);
            default -> {
                log.warn("No valid SubCommandName found");
                event.getHook().editOriginal("No valid SubCommandName found").queue();
            }
        }
    }

    private boolean checkConfig(SlashCommandInteractionEvent event, String guildId, String optionName) {
        assert event.getGuild() != null;
        if (!SpeedDatingManager.getInstance().getGuildConfigMap().containsKey(event.getGuild().getId())) {
            event.getHook().editOriginal("Please setup the speed-dating channels before %s!".formatted(optionName)).queue();
            return true;
        }
        return false;
    }

    private boolean checkActiveEvent(SlashCommandInteractionEvent event, String guildId, String optionName) {
        assert event.getGuild() != null;

        return false;
    }

    public void startSpeedDating(SlashCommandInteractionEvent event) {
        assert event.getGuild() != null;
        if (checkConfig(event, event.getGuild().getId(), "starting")) {
            return;
        }
        if (SpeedDatingManager.getInstance().getActiveSpeedDatingMap().containsKey(event.getGuild().getId())) {
            event.getHook().editOriginal("There is already a event running!").queue();
            return;
        }
        SpeedDatingManager.getInstance().getActiveSpeedDatingMap().put(
                event.getGuild().getId(),
                new SpeedDatingEvent(event.getGuild()));
        event.getHook().editOriginal("Starting Speed-Dating Event").queue();
    }

    public void stopSpeedDating(SlashCommandInteractionEvent event) {
        assert event.getGuild() != null;
        if (checkConfig(event, event.getGuild().getId(), "stopping")) {
            return;
        }
        if (!SpeedDatingManager.getInstance().getActiveSpeedDatingMap().containsKey(event.getGuild().getId())) {
            event.getHook().editOriginal("There is no a event running to stop!").queue();
            return;
        }
        SpeedDatingManager.getInstance().getActiveSpeedDatingMap().get(event.getGuild().getId()).stopEvent();
        event.getHook().editOriginal("Starting Speed-Dating Event").queue();
    }

    public void setupSpeedDating(SlashCommandInteractionEvent event) {
        // Get the options from the command
        OptionMapping categoryOption = event.getOption("category");
        OptionMapping voiceChannelOption = event.getOption("voice_channel");

        assert event.getGuild() != null;
        assert categoryOption != null;
        assert voiceChannelOption != null;

        Category category = categoryOption.getAsChannel().asCategory();
        VoiceChannel voiceChannel = voiceChannelOption.getAsChannel().asVoiceChannel();

        SpeedDatingManager.getInstance().getGuildConfigMap().remove(event.getGuild().getId());
        SpeedDatingManager.getInstance().getGuildConfigMap().put(
                event.getGuild().getId(),
                new SpeedDatingConfig(
                        event.getJDA().getSelfUser().getId(),
                        event.getGuild(),
                        category,
                        voiceChannel)
        );

        event.getHook().editOriginal("Speed dating channels set up successfully!").queue();
    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[]{Permission.MANAGE_CHANNEL};
    }

    @Override
    public CommandType getType() {
        return CommandType.GUILD_ONLY;
    }
}
