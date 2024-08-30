package de.quantum.modules.custombot;

import de.quantum.core.events.EventReflector;
import de.quantum.core.module.ModuleAnnotation;
import de.quantum.core.shutdown.ShutdownAnnotation;
import de.quantum.core.shutdown.ShutdownInterface;
import de.quantum.core.utils.Secret;
import de.quantum.core.utils.Utils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ConcurrentSessionController;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.EnumSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

@Slf4j
@ModuleAnnotation(
        moduleName = "CustomBot",
        moduleDescription = "Starting another Discord Bot with specific Token, which has the same functionality as Quantum.",
        moduleVersion = "v0.0.1",
        moduleAuthorName = "kylederzweite",
        moduleAuthorID = "378542649579143188"
)
@ShutdownAnnotation
public class CustomBotManager implements ShutdownInterface {
    private static volatile CustomBotManager INSTANCE = null;

    @Getter
    private final ConcurrentHashMap<String, JDA> customJdaInstanceMap;

    private CustomBotManager() {
        if (INSTANCE != null) {
            throw new AssertionError(
                    "Another instance of "
                            + CustomBotManager.class.getName()
                            + " class already exists, Can't create a new instance.");
        }
        this.customJdaInstanceMap = new ConcurrentHashMap<>();
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

    public void startBot(String botId) {
        stopBot(botId);
        String token = CustomBotDatabaseManager.getCustomBotToken(botId);
        if (token == null) {
            return;
        }
        JDA jda = getCustomBotJda(token);
        customJdaInstanceMap.put(botId, jda);
        updateBotActivity(jda.getSelfUser().getId());
    }

    public void stopBot(String botId) {
        JDA botJda = customJdaInstanceMap.getOrDefault(botId, null);
        if (botJda != null) {
            log.warn("Shutting down {}", Utils.getJdaShardGuildCountString(botJda));
            botJda.shutdown();
            botJda.shutdownNow();
        }
    }

    public JDA getTestCustomBotJDA(String token) {
        token = Secret.decrypt(token);
        return JDABuilder.createLight(token).build();
    }

    public JDA getCustomBotJda(String token) {
        token = Secret.decrypt(token);
        return JDABuilder.create(token, EnumSet.allOf(GatewayIntent.class))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .enableCache(EnumSet.allOf(CacheFlag.class))
                .setSessionController(new ConcurrentSessionController())
                .setCallbackPool(Executors.newFixedThreadPool(1), true)
                .setBulkDeleteSplittingEnabled(false)
                .setRawEventsEnabled(true)
                .addEventListeners(new EventReflector()).build();
    }

    public void startAll() {
        CustomBotDatabaseManager.getCustomBotTokens().forEach(encryptedToken -> {
            String token = Secret.decrypt(encryptedToken);
            JDA jda = getCustomBotJda(token);
            String botId = jda.getSelfUser().getId();
            customJdaInstanceMap.put(botId, jda);
            updateBotActivity(botId);
        });
    }

    public void stopAll() {
        customJdaInstanceMap.forEach((botId, jda) -> {
            log.info("Shutting down {}", Utils.getJdaShardGuildCountString(jda));
            jda.shutdown();
        });
        customJdaInstanceMap.clear();
    }

    public void updateBotActivity(String botId) {
        JDA customBotJda = customJdaInstanceMap.getOrDefault(botId, null);
        if (customBotJda == null) return;
        Activity newActivity = CustomBotDatabaseManager.getCustomBotActivity(botId);
        new Thread(() -> updateBotActivity(customBotJda, newActivity)).start();
    }

    public void updateBotActivity(JDA jda, Activity newActivity) {
        try {
            jda.awaitReady();
            jda.getPresence().setActivity(newActivity);
        } catch (InterruptedException e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void shutdown() {
        stopAll();
    }
}