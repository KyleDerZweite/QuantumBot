package de.quantum.core.commands;

import de.quantum.core.database.DatabaseManager;

import java.sql.ResultSet;

public class CommandCooldown {

    public static boolean isCommandCooldown(long commandId) {
        return false;
    }

    private ResultSet getCommandCooldowns() {
        return DatabaseManager.getInstance().selectFrom("*", "command_cooldown");
    }




}
