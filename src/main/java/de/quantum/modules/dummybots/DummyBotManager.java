package de.quantum.modules.dummybots;

import de.quantum.core.events.EventReflector;
import de.quantum.core.module.ModuleAnnotation;
import de.quantum.core.shutdown.ShutdownAnnotation;
import de.quantum.core.shutdown.ShutdownInterface;
import de.quantum.core.utils.CheckUtils;
import de.quantum.core.utils.Secret;
import de.quantum.core.utils.StatusUtils;
import de.quantum.core.utils.Utils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.utils.ConcurrentSessionController;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ShutdownAnnotation
@ModuleAnnotation(
        moduleName = "DummyBot",
        moduleDescription = "DummyBots feature - Access, request only!",
        moduleVersion = "v0.0.2",
        moduleAuthorName = "kylederzweite",
        moduleAuthorID = "378542649579143188"
)
public class DummyBotManager implements ShutdownInterface {

    private static volatile DummyBotManager INSTANCE = null;

    @Getter
    private final ConcurrentHashMap<String, ArrayList<JDA>> dummyBotsMap;

    @Getter
    private final StatusUtils statusUtils;

    private DummyBotManager() {
        if (INSTANCE != null) {
            throw new AssertionError(
                    "Another instance of "
                            + DummyBotManager.class.getName()
                            + " class already exists, Can't create a new instance.");
        }
        this.statusUtils = new StatusUtils("dummy_bot_status");
        this.dummyBotsMap = new ConcurrentHashMap<>();
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

    public void startBotsFromGuild(String guildId) {
        stopBotsFromGuild(guildId);
        ArrayList<JDA> jdaList = new ArrayList<>();
        ArrayList<String> dummyBotToken = DummyBotDatabaseManager.getGuildDummyBotTokens(guildId);
        dummyBotToken.forEach(token -> {
            JDA jda = getDummyBotJDA(token);
            jdaList.add(jda);
        });
        dummyBotsMap.put(guildId, jdaList);
    }

    public void stopBotsFromGuild(String guildId) {
        ArrayList<JDA> jdaList = dummyBotsMap.getOrDefault(guildId, null);
        jdaList.forEach(JDA::shutdown);
        dummyBotsMap.remove(guildId);
    }

    public void startBotId(String guildId, String botId) {
        stopBotId(guildId, botId);
        String token = DummyBotDatabaseManager.getDummyBotToken(botId);
        if (token == null) {
            return;
        }
        JDA jda = getDummyBotJDA(token);
        if (!dummyBotsMap.containsKey(guildId)) {
            ArrayList<JDA> jdaList = new ArrayList<>();
            dummyBotsMap.put(guildId, jdaList);
        }
        dummyBotsMap.get(guildId).add(jda);
    }

    public void stopBotId(String guildId, String botId) {
        ArrayList<JDA> jdaList = dummyBotsMap.getOrDefault(guildId, null);
        if (jdaList == null) return;
        for (JDA jda : jdaList) {
            if (jda.getSelfUser().getId().equals(botId)) {
                jda.shutdown();
                jdaList.remove(jda);
                break;
            }
        }
    }

    public void startAll() {
        stopAll();
        DummyBotDatabaseManager.getDummyBots().forEach((guildId, dummyBotsArray) -> {
            ArrayList<JDA> jdaList = new ArrayList<>();
            dummyBotsArray.forEach((token) -> {
                JDA jda = getDummyBotJDA(token);
                jdaList.add(jda);
            });
            dummyBotsMap.put(guildId, jdaList);
        });
    }

    public void stopAll() {
        if (dummyBotsMap.isEmpty()) {
            return;
        }
        dummyBotsMap.forEach((guildId, jdaList) -> jdaList.forEach((jda) -> {
            jda.getPresence().setPresence(OnlineStatus.OFFLINE, null);
            jda.shutdown();
            log.debug("Shutting down {}", Utils.getJdaShardGuildCountString(jda));
        }));
        dummyBotsMap.clear();
    }

    public ArrayList<JDA> getGuildDummyJdaInstances(String guildId) {
        return dummyBotsMap.get(guildId);
    }

    public JDA getTestDummyJDA(String token) {
        token = Secret.decrypt(token);
        return JDABuilder.createLight(token).build();
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
            stopAll();
            log.info("Shutting down completed");
        }
    }


}