package de.quantum.modules.custombot;

import de.quantum.core.events.EventReflector;
import de.quantum.core.module.ModuleAnnotation;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ConcurrentSessionController;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.EnumSet;
import java.util.concurrent.Executors;

@ModuleAnnotation(
        moduleName = "CustomBot",
        moduleDescription = "Starting another Discord Bot with specific Token, which has the same functionality as Quantum.",
        moduleVersion = "v0.0.1",
        moduleAuthorName = "kylederzweite",
        moduleAuthorID = "378542649579143188"
)
public class CustomBotManager {
    private static volatile CustomBotManager INSTANCE = null;

    private CustomBotManager() {
        if (INSTANCE != null) {
            throw new AssertionError(
                    "Another instance of "
                            + CustomBotManager.class.getName()
                            + " class already exists, Can't create a new instance.");
        }
    }

    public static CustomBotManager getInstance() {
        if (INSTANCE == null) {
            synchronized (CustomBotManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CustomBotManager();
                }
            }
        }
        return INSTANCE;
    }

    public JDA getCustomBotJDA(String token) {
        return JDABuilder.create(token, EnumSet.allOf(GatewayIntent.class))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .enableCache(EnumSet.allOf(CacheFlag.class))
                .setSessionController(new ConcurrentSessionController())
                .setCallbackPool(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()), true)
                .setBulkDeleteSplittingEnabled(false)
                .setRawEventsEnabled(true)
                .addEventListeners(new EventReflector()).build();
    }

}