package de.quantum;


import de.quantum.core.ShardMan;
import de.quantum.core.commands.CommandManager;
import de.quantum.core.database.DatabaseConfig;
import de.quantum.core.database.DatabaseManager;
import de.quantum.core.module.ModuleManager;
import de.quantum.core.shutdown.ShutdownManager;
import de.quantum.core.utils.ConsoleScanner;
import de.quantum.core.utils.Secret;
import de.quantum.modules.custombot.CustomBotManager;
import de.quantum.modules.dummybots.DummyBotManager;

import java.util.Arrays;


public class Main {

    public static String SECRET_KEY;
    public static String TOKEN;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar QuantumBot.jar <secret key> <token>");
            System.exit(1);
        }
        SECRET_KEY = args[0].replace("'","\"");
        TOKEN = args[1];
        //TODO REMOVE!
        System.out.println(SECRET_KEY + " " + TOKEN);

        DatabaseManager.getInstance().init();
        ModuleManager.getInstance().init();
        CommandManager.getInstance().loadCommands();
        ConsoleScanner.getInstance().start();

        Runtime.getRuntime().addShutdownHook(new Thread(ShutdownManager::shutdown));

        new Thread(() -> DummyBotManager.getInstance().startAll()).start();
        new Thread(() -> CustomBotManager.getInstance().startAll()).start();
        ShardMan.init();
    }
}