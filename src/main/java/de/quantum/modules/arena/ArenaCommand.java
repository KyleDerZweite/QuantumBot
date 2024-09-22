package de.quantum.modules.arena;

import de.quantum.core.commands.CommandAnnotation;
import de.quantum.core.commands.CommandInterface;
import de.quantum.core.module.ModuleCommand;
import de.quantum.core.utils.CheckUtils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

@ModuleCommand(
        moduleName = "Arena"
)
public class ArenaCommand implements CommandInterface<SlashCommandInteractionEvent> {

    @Override
    public CommandDataImpl getCommandData() {
        return new CommandDataImpl("arena", "The Arena command to start different vote battles between character")
                .addSubcommands(
                        new SubcommandData("schedule", "Schedules random arenas"),
                        new SubcommandData("start", "Starts a new arena battle"),
                        new SubcommandData("stop", "Stops an arena battle"),
                        new SubcommandData("list", "Lists all possibles arenas"),
                        new SubcommandData("setup", "To setup the arena mini-game in your server")
                )
                .addSubcommandGroups(
                        new SubcommandGroupData("image", "A settings command for image submissions")
                                .addSubcommands(
                                        new SubcommandData("add", "Create a request to add a new Series"),
                                        new SubcommandData("submit", "Submissions an image to an existing pool"),
                                        new SubcommandData("list", "Lists all images of that arena"),
                                        new SubcommandData("change", "Creates a change request")
                                )
                );
    }

    @Override
    public void perform(SlashCommandInteractionEvent event) {
        assert event.getGuild() != null;
        String commandStatement = "";
        if (CheckUtils.checkNotNull(event.getSubcommandGroup())) {
            commandStatement += event.getSubcommandGroup() + "_";
        }
        commandStatement += event.getSubcommandName();
        switch (commandStatement) {
            case "start" -> handleStart(event);
            case "stop" -> handleStop(event);
            case "schedule" -> handleSchedule(event);
            case "list" -> handleList(event);
            case "setup" -> handleSetup(event);
            case "image_add" -> handleImageAdd(event);
            case "image_submit" -> handleImageSubmit(event);
            case "image_list" -> handleImageList(event);
            case "image_change" -> handleImageChange(event);
        }
    }

    public void handleStart(SlashCommandInteractionEvent event) {

    }

    public void handleStop(SlashCommandInteractionEvent event) {

    }

    public void handleSchedule(SlashCommandInteractionEvent event) {

    }

    public void handleList(SlashCommandInteractionEvent event) {

    }

    public void handleSetup(SlashCommandInteractionEvent event) {

    }

    public void handleImageAdd(SlashCommandInteractionEvent event) {

    }

    public void handleImageSubmit(SlashCommandInteractionEvent event) {

    }

    public void handleImageList(SlashCommandInteractionEvent event) {

    }

    public void handleImageChange(SlashCommandInteractionEvent event) {

    }


}
