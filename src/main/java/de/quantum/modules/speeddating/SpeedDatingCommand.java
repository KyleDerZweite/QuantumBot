package de.quantum.modules.speeddating;

import de.quantum.core.commands.CommandInterface;
import de.quantum.core.module.ModuleCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

@ModuleCommand(moduleName = "SpeedDating")
public class SpeedDatingCommand implements CommandInterface<SlashCommandInteractionEvent> {

    @Override
    public CommandDataImpl getCommandData() {
        return new CommandDataImpl("speed-dating", "Starts / Stops the speed-dating event").addSubcommands(
                new SubcommandData("start", ""),
                new SubcommandData("stop", "")
        );
    }

    @Override
    public void perform(SlashCommandInteractionEvent event) {

    }

    @Override
    public Permission[] getPermissions() {
        return CommandInterface.super.getPermissions();
    }
}
