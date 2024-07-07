package de.quantum;


import de.quantum.core.ShardMan;
import de.quantum.core.commands.CommandManager;
import de.quantum.core.database.DatabaseManager;
import de.quantum.core.module.ModuleManager;
import de.quantum.core.shutdown.ShutdownManager;
import de.quantum.modules.dummybots.DummyBotManager;

import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
        new Main();
    }

    private Main() {
        DatabaseManager.getInstance().init();
        ModuleManager.getInstance().init();
        CommandManager.getInstance().loadCommands();

        Runtime.getRuntime().addShutdownHook(new Thread(ShutdownManager::shutdown));
        new Thread(this::checkStop).start();

        new Thread(() -> DummyBotManager.getInstance().startAll()).start();
        ShardMan.init();

    }

    public void checkStop() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String userInput = scanner.nextLine();
            if (userInput.equalsIgnoreCase("stop")) {
                System.exit(0);
                break;
            }
        }
        scanner.close();
    }
}