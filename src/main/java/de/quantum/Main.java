package de.quantum;


import de.quantum.core.LanguageManager;
import de.quantum.core.ShardMan;
import de.quantum.core.database.DatabaseManager;


public class Main {

    public static void main(String[] args) {
        new Main();
    }

    private Main() {
        LanguageManager.loadLanguages();
        DatabaseManager.getInstance().init();

        ShardMan.init();
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void shutdown() {
        ShardMan.getInstance().shutdown();
    }

}