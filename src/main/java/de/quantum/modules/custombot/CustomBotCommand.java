package de.quantum.modules.custombot;

import de.quantum.core.commands.CommandAnnotation;
import de.quantum.core.commands.CommandInterface;
import de.quantum.core.commands.CommandType;
import de.quantum.core.events.EventReflector;
import de.quantum.core.module.ModuleCommand;
import de.quantum.core.utils.Utils;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ConcurrentSessionController;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.concurrent.Executors;

@CommandAnnotation
@ModuleCommand(moduleName = "CustomBot")
public class CustomBotCommand implements CommandInterface<SlashCommandInteractionEvent> {


    @Override
    public CommandDataImpl getCommandData() {
        ArrayList<Command.Choice> choices = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Activity.ActivityType activityType = Activity.ActivityType.fromKey(i);
            choices.add(new Command.Choice(activityType.name(), activityType.getKey()));
        }
        return new CommandDataImpl("custom_bot", "Starts / Stops / Updates the SubBot").addSubcommands(
                new SubcommandData("start", "Start the SubBot").addOptions(
                        new OptionData(OptionType.STRING, "bot_token", "The Token of your bot u want to start. This token will be stored encrypted!", true),
                        new OptionData(OptionType.INTEGER, "activity_type", "The type of the activity your bot gonna have (can be changed later)", false)
                                .addChoices(choices),
                        new OptionData(OptionType.STRING, "activity_name", "The activity name. Example: Playing <activity_name>", false),
                        new OptionData(OptionType.STRING, "activity_url", "The activity url. Supported by types: Watching, Streaming", false)
                ),
                new SubcommandData("stop", "Stops the SubBot").addOptions(
                        new OptionData(OptionType.STRING, "bot_id", "The Bot id which should be shutdown. Requires the executing Member to be in the BotDev Team", true)
                ),
                new SubcommandData("update", "Updates the SubBot").addOptions(
                        new OptionData(OptionType.INTEGER, "activity_type", "The type of the activity your bot gonna have (can be changed later)", true)
                                .addChoices(choices),
                        new OptionData(OptionType.STRING, "activity_name", "The activity name. Example: Playing <activity_name>", true),
                        new OptionData(OptionType.STRING, "activity_url", "The activity url. Supported by types: Watching, Streaming", false)
                )
        );
    }

    @Override
    public void perform(SlashCommandInteractionEvent event) {
        


        JDABuilder jdaBuilder = JDABuilder.create(Utils.getToken(), EnumSet.allOf(GatewayIntent.class))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .enableCache(EnumSet.allOf(CacheFlag.class))
                .setSessionController(new ConcurrentSessionController())
                .setCallbackPool(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()), true)
                .setBulkDeleteSplittingEnabled(false)
                .setRawEventsEnabled(true)
                .addEventListeners(new EventReflector());
    }

    @Override
    public Permission[] getPermissions() {
        return new Permission[]{Permission.ADMINISTRATOR};
    }

    @Override
    public CommandType getType() {
        return CommandType.SUPPORT_GUILD_ONLY;
    }
}
