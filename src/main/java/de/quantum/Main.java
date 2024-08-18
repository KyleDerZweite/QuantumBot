package de.quantum;


import de.quantum.core.ShardMan;
import de.quantum.core.commands.CommandManager;
import de.quantum.core.database.DatabaseManager;
import de.quantum.core.module.ModuleManager;
import de.quantum.core.shutdown.ShutdownManager;
import de.quantum.core.utils.ConsoleScanner;
import de.quantum.modules.dummybots.DummyBotManager;

import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
        DatabaseManager.getInstance().init();
        ModuleManager.getInstance().init();
        CommandManager.getInstance().loadCommands();
        ConsoleScanner.getInstance().start();

        Runtime.getRuntime().addShutdownHook(new Thread(ShutdownManager::shutdown));

        new Thread(() -> DummyBotManager.getInstance().startAll()).start();
        ShardMan.init();
    }
}