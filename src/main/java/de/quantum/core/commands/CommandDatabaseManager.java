package de.quantum.core.commands;

import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;

public class CommandDatabaseManager {

    public static final String COMMAND_COOLDOWN_TABLE_NAME = "command_cooldown";

    public long getCommandCooldown(@Nullable Guild guild, String commandId) {
        if (guild == null) {
            // Database query for the default command cooldown
            return 0L;
        }
        // Datebase quere with guild id and command id
        return 1L;
    }

}
