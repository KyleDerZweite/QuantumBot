package de.quantum.modules.speeddating;

import de.quantum.core.commands.CommandAnnotation;
import de.quantum.core.commands.CommandInterface;
import de.quantum.core.commands.CommandType;
import de.quantum.core.module.ModuleCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

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
                                new OptionData(OptionType.CHANNEL, "hub_channel", "The Channel where the users that want to participate join.", false),
                                new OptionData(OptionType.CHANNEL, "waiting_channel", "The Channel where users wait in between rounds / or if the user count is uneven.", false)
                        )
        ).setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MODERATE_MEMBERS));
    }

    @Override
    public void perform(SlashCommandInteractionEvent event) {

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
