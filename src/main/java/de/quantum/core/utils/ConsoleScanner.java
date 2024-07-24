package de.quantum.core.utils;

import java.util.Scanner;

public class ConsoleScanner {

    private static volatile ConsoleScanner INSTANCE = null;

    private ConsoleScanner() {
        if (INSTANCE != null) {
            throw new AssertionError(
                    "Another instance of "
                            + ConsoleScanner.class.getName()
                            + " class already exists, Can't create a new instance.");
        }
    }

    public static ConsoleScanner getInstance() {
        if (INSTANCE == null) {
            synchronized (ConsoleScanner.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ConsoleScanner();
                }
            }
        }
        return INSTANCE;
    }

    public void start() {
        new Thread(this::checkStop).start();
    }

    private void checkStop() {
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
