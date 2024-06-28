package de.luxury;


import de.luxury.core.ShardMan;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;


public class Main {

    public static void main(String[] args) {
        new Main();
    }

    private Main() {
        ShardMan.init();
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public void shutdown() {
        ShardMan.getInstance().shutdown();
    }

}