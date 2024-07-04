package de.quantum;


import de.quantum.core.LanguageManager;
import de.quantum.core.ShardMan;
import de.quantum.core.database.DatabaseManager;

import java.util.Scanner;


public class Main {

    public static void main(String[] args) {
        new Main();
    }

    private Main() {
        DatabaseManager.getInstance().init();

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        new Thread(this::checkStop).start();

        ShardMan.init();
    }

    public void checkStop() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String userInput = scanner.nextLine();
            if (userInput.equalsIgnoreCase("stop")) {
                shutdown();
                break;
            }
        }
        scanner.close();
    }

    public void shutdown() {
        ShardMan.getInstance().shutdown();
    }

}