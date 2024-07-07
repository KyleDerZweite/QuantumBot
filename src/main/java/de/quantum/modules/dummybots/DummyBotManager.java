package de.quantum.modules.dummybots;

import de.quantum.core.database.DatabaseManager;
import de.quantum.core.events.EventReflector;
import de.quantum.core.shutdown.ShutdownAnnotation;
import de.quantum.core.shutdown.ShutdownInterface;
import de.quantum.core.utils.CheckUtils;
import de.quantum.core.utils.Secret;
import de.quantum.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.utils.ConcurrentSessionController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ShutdownAnnotation
public class DummyBotManager implements ShutdownInterface {
    private static volatile DummyBotManager INSTANCE = null;

    private ConcurrentHashMap<String, JDA> dummyBotsMap = null;

    private DummyBotManager() {
        if (INSTANCE != null) {
            throw new AssertionError(
                    "Another instance of "
                            + DummyBotManager.class.getName()
                            + " class already exists, Can't create a new instance.");
        }
    }

    public static DummyBotManager getInstance() {
        if (INSTANCE == null) {
            synchronized (DummyBotManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DummyBotManager();
                }
            }
        }
        return INSTANCE;
    }

    public void startBotId(String botId) {
        //TODO
    }

    public void stopBot(String botId) {
        //TODO
    }

    public void startAll() {
        dummyBotsMap = new ConcurrentHashMap<>();
        for (Map.Entry<String, String> dummyEntry : DatabaseManager.getInstance().getDummyBots().entrySet()) {
            JDA jda = getDummyBotJDA(dummyEntry.getValue());
            dummyBotsMap.put(dummyEntry.getKey(), jda);
        }
    }

    public void stopAll() {
        if (dummyBotsMap == null || dummyBotsMap.isEmpty()) {
            return;
        }
        for (Map.Entry<String, JDA> dummyEntry : dummyBotsMap.entrySet()) {
            JDA jda = dummyEntry.getValue();
            jda.getPresence().setPresence(OnlineStatus.OFFLINE, null);
            jda.shutdown();
            jda.shutdownNow();
        }
    }

    public boolean isDummyBot(String botId) {
        return DatabaseManager.getInstance().getDummyBots().containsKey(botId);
    }

    public JDA getDummyBotJDA(String token) {
        token = Secret.decrypt(token);
        return JDABuilder.createLight(token)
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .setSessionController(new ConcurrentSessionController())
                .addEventListeners(new EventReflector())
                .build();
    }

    @Override
    public void shutdown() {
        if (CheckUtils.checkNotNull(dummyBotsMap)) {
            log.warn("Shutting down dummy bots");
            for (JDA jda : dummyBotsMap.values()) {
                jda.getPresence().setPresence(OnlineStatus.OFFLINE, null);
                log.info("Shutting down {}", Utils.getJdaShardGuildCountString(jda));
                jda.shutdown();
            }
            this.dummyBotsMap = null;
            log.info("Shutting down completed");
        }
    }
}