package de.quantum.modules.custombot;

import de.quantum.commands.misc.StatusCommand;
import de.quantum.core.commands.CommandAnnotation;
import de.quantum.core.commands.CommandInterface;
import de.quantum.core.commands.CommandType;
import de.quantum.core.module.ModuleCommand;
import de.quantum.core.utils.Secret;
import de.quantum.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@CommandAnnotation
@ModuleCommand(moduleName = "CustomBot")
public class CustomBotCommand implements CommandInterface<SlashCommandInteractionEvent> {


    @Override
    public CommandDataImpl getCommandData() {
        ArrayList<Command.Choice> choices = new ArrayList<>();
        for (Activity.ActivityType activityType : Activity.ActivityType.values()) {
            choices.add(new Command.Choice(activityType.name(), activityType.getKey()));
        }
        return new CommandDataImpl("custom_bot", "Starts / Stops / Updates the SubBot").addSubcommands(
                new SubcommandData("add", "Start the SubBot").addOptions(
                        new OptionData(OptionType.STRING, "token", "The Token of your bot u want to start. This token will be stored encrypted!", true),
                        new OptionData(OptionType.INTEGER, "activity_type", "The type of the activity your bot gonna have (can be changed later)", false)
                                .addChoices(choices),
                        new OptionData(OptionType.STRING, "activity_name", "The activity name. Example: Playing <activity_name>", false),
                        new OptionData(OptionType.STRING, "activity_url", "The activity url. Supported by types: Watching, Streaming", false)
                ),
                new SubcommandData("start", "Start the SubBot").addOptions(
                        new OptionData(OptionType.STRING, "bot_id", "The Bot id which should be started", true)
                ),
                new SubcommandData("stop", "Stops the SubBot").addOptions(
                        new OptionData(OptionType.STRING, "bot_id", "The Bot id which should be shutdown", true)
                ),
                new SubcommandData("update", "Updates the SubBot").addOptions(
                        new OptionData(OptionType.STRING, "bot_id", "The Bot id which should be updated", true),
                        new OptionData(OptionType.INTEGER, "activity_type", "The type of the activity your bot gonna have (can be changed later)", true)
                                .addChoices(choices),
                        new OptionData(OptionType.STRING, "activity_name", "The activity name. Example: Playing <activity_name>", true),
                        new OptionData(OptionType.STRING, "activity_url", "The activity url. Supported by types: Watching, Streaming", false)
                ),
                new SubcommandData("status", "Shows the Status of the current running custom-bots")
        );
    }


    @Override
    public void perform(SlashCommandInteractionEvent event) {
        if (event.getSubcommandName() == null) {
            event.getHook().editOriginal("Something went wrong!\nPlease try again with the desired subcommand.").queue();
            return;
        }

        switch (event.getSubcommandName()) {
            case "add" -> handleAdd(event);
            case "start" -> handleStart(event);
            case "stop" -> handleStop(event);
            case "update" -> handleUpdate(event);
            case "status" -> handleStatus(event);
            default ->
                    event.getHook().editOriginal("Something went wrong!\nPlease try again with the desired subcommand.").queue();
        }
    }

    public void handleStart(SlashCommandInteractionEvent event) {
        OptionMapping tokenMapping = event.getOption("token");
        if (tokenMapping == null) {
            event.getHook().editOriginal("Please provide a valid bot id!").queue();
            return;
        }
        String botId = tokenMapping.getAsString();
        CustomBotManager.getInstance().startBot(botId);
        event.getHook().editOriginal("Started <@" + botId + ">").queue();
    }

    public void handleAdd(SlashCommandInteractionEvent event) {
        if (event.getOption("token") == null) {
            event.getHook().editOriginal("Please provide a valid token!").queue();
            return;
        }
        String token = Objects.requireNonNull(event.getOption("token")).getAsString();
        String encryptedToken = Secret.encrypt(token);

        String botId;
        String botString;
        try {
            JDA newCustomBotJDA = CustomBotManager.getInstance().getTestCustomBotJDA(token);
            botId = newCustomBotJDA.getSelfUser().getId();
            String guildId = Objects.requireNonNull(event.getGuild()).getId();
            CustomBotDatabaseManager.addCustomBot(botId, encryptedToken, guildId);
            botString = Utils.getJdaShardGuildCountString(newCustomBotJDA);
            newCustomBotJDA.shutdown();
            newCustomBotJDA.shutdownNow();
        } catch (InvalidTokenException ignored) {
            event.getHook().editOriginal("Please provide a valid token!").queue();
            return;
        }

        OptionMapping activityTypeMapping = event.getOption("activity_type");
        OptionMapping activityNameMapping = event.getOption("activity_name");
        OptionMapping activityUrlMapping = event.getOption("activity_url");
        if (activityTypeMapping != null && activityNameMapping != null) {
            int activityTypeId = activityTypeMapping.getAsInt();
            String activityName = activityNameMapping.getAsString();
            String activityUrl = null;
            if (activityUrlMapping != null) {
                activityUrl = activityUrlMapping.getAsString();
            }
            CustomBotDatabaseManager.updateActivity(event.getJDA().getSelfUser().getId(), activityTypeId, activityName, activityUrl);
        }
        CustomBotManager.getInstance().startBot(botId);
        event.getHook().editOriginal("Started %s".formatted(botString)).queue();
    }

    public void handleStop(SlashCommandInteractionEvent event) {
        if (event.getOption("bot_id") == null) {
            event.getHook().editOriginal("Please provide a valid Bot ID!").queue();
            return;
        }
        String botId = Objects.requireNonNull(event.getOption("bot_id")).getAsString();
        CustomBotManager.getInstance().stopBot(botId);
    }

    public void handleUpdate(SlashCommandInteractionEvent event) {
        String botId = Objects.requireNonNull(event.getOption("bot_id")).getAsString();
        int activityTypeId = Objects.requireNonNull(event.getOption("activity_type")).getAsInt();
        String activityName = Objects.requireNonNull(event.getOption("activity_name")).getAsString();
        OptionMapping activityUrlMapping = event.getOption("activity_url");
        String activityUrl = null;
        if (activityUrlMapping != null) {
            activityUrl = activityUrlMapping.getAsString();
        }
        CustomBotDatabaseManager.updateActivity(botId, activityTypeId, activityName, activityUrl);
        CustomBotManager.getInstance().updateBotActivity(botId);
        event.getHook().editOriginal("Updated <@" + botId + ">").queue();
    }

    public void handleStatus(SlashCommandInteractionEvent event) {
        //TODO
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
