package de.quantum.core.commands;

import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.utils.config.AuthorizationConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandManagerTest {

    @Test
    void getInstance() {
        assertNotNull(CommandManager.getInstance());
    }

    @Test
    void registerCommands() {
        assertDoesNotThrow(() -> CommandManager.getInstance().registerCommands(null));
    }

    @Test
    void deleteUnusedCommands() {
        assertDoesNotThrow(() -> CommandManager.getInstance().deleteUnusedCommands(null));
    }

    @Test
    void localizeCommandDescription() {
    }

    @Test
    void getCommandHashMap() {
        assertNotNull(CommandManager.getInstance().getCommandHashMap());
    }
}