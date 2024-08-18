package de.quantum.modules.speeddating;

import de.quantum.core.commands.CommandAnnotation;
import de.quantum.core.commands.CommandInterface;
import de.quantum.core.commands.CommandType;
import de.quantum.core.module.ModuleCommand;
import de.quantum.modules.speeddating.entities.SpeedDatingConfig;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
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
                                new OptionData(OptionType.CHANNEL, "category", "The Category for the system.", false),
                                new OptionData(OptionType.CHANNEL, "voice_channel", "The Channel where the users that want to participate join.", false)
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

    public void startSpeedDating(SlashCommandInteractionEvent event) {

    }

    public void stopSpeedDating(SlashCommandInteractionEvent event) {

    }

    public void setupSpeedDating(SlashCommandInteractionEvent event) {
        // Get the options from the command
        OptionMapping categoryOption = event.getOption("category");
        OptionMapping voiceChannelOption = event.getOption("voice_channel");

        Category category;
        VoiceChannel voiceChannel;

        if (event.getGuild() == null) {
            event.getHook().editOriginal("No guild found").queue();
            return;
        }

        if (categoryOption == null) {
            category = event.getGuild().createCategory("SpeedDatingCategory").submit().join();
        } else {
            category = categoryOption.getAsChannel().asCategory();
        }

        if (voiceChannelOption == null) {
            voiceChannel = event.getGuild().createVoiceChannel("SpeedDatingChannel", category).submit().join();
        } else {
            voiceChannel = voiceChannelOption.getAsChannel().asVoiceChannel();
        }

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
        return new Permission[]{Permission.MANAGE_SERVER};
    }

    @Override
    public CommandType getType() {
        return CommandType.GUILD_ONLY;
    }
}
