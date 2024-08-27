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
        new Thread(this::checkConsoleInput).start();
    }

    private void checkConsoleInput() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String userInput = scanner.nextLine();
            String inputIdentifier = userInput.split(" ")[0];
            switch (inputIdentifier) {
                case "stop" -> {
                    scanner.close();
                    System.exit(0);
                }
                case "encrypt" -> {
                    String inputValue = userInput.split(" ")[1];
                    System.out.println(Secret.encrypt(inputValue));
                }
            }
        }

    }

}
