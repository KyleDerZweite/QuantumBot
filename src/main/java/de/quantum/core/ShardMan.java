package de.quantum.core;

import de.quantum.core.events.EventReflector;
import de.quantum.core.shutdown.ShutdownAnnotation;
import de.quantum.core.shutdown.ShutdownInterface;
import de.quantum.core.utils.CheckUtils;
import de.quantum.core.utils.Utils;
import de.quantum.modules.custombot.CustomBotDatabaseManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ConcurrentSessionController;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.util.EnumSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

@Slf4j
@ShutdownAnnotation(shutdownLast = true)
public class ShardMan extends ListenerAdapter implements ShutdownInterface {

    private static volatile ShardMan INSTANCE = null;

    @Getter
    private ShardManager shardManager;

    @Getter
    private final ConcurrentHashMap<String, JDA> jdaInstanceHashMap;

    private ShardMan() {
        if (CheckUtils.checkNotNull(INSTANCE)) {
            throw new AssertionError(
                    "Another instance of "
                            + ShardMan.class.getName()
                            + " class already exists, Can't create a new instance.");
        }

        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder
                .create(Utils.getToken(), EnumSet.allOf(GatewayIntent.class))
                .setStatus(OnlineStatus.DO_NOT_DISTURB)
                .enableCache(EnumSet.allOf(CacheFlag.class))
                .setSessionController(new ConcurrentSessionController())
                .setCallbackPool(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()), true)
                .setBulkDeleteSplittingEnabled(false)
                .setRawEventsEnabled(true)
                .setShardsTotal(Utils.TOTAL_SHARDS)
                .addEventListeners(new EventReflector());
        this.shardManager = builder.build();
        this.jdaInstanceHashMap = new ConcurrentHashMap<>();

        shardManager.getGuilds().forEach(guild -> {
            if (CustomBotDatabaseManager.guildHasCustomBot(guild.getId())) {
                guild.leave().submit().join();
            }
        });
    }

    public static ShardMan getInstance() {
        init();
        return INSTANCE;
    }

    public JDA getJDAInstance(String botId) {
        return jdaInstanceHashMap.get(botId);
    }

    public Guild getGuildByJdaAndId(String botId, String guildId) {
        JDA jda = getJDAInstance(botId);
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            guild = shardManager.getGuildById(guildId);
        }
        return guild;
    }

    public static void init() {
        if (CheckUtils.checkNull(INSTANCE)) {
            synchronized (ShardMan.class) {
                if (CheckUtils.checkNull(INSTANCE)) {
                    INSTANCE = new ShardMan();
                }
            }
        }
    }

    @Override
    public void shutdown() {
        if (CheckUtils.checkNotNull(shardManager)) {
            log.warn("Shutting down shard manager");
            for (JDA jda : shardManager.getShards()) {
                jda.getPresence().setPresence(OnlineStatus.OFFLINE, null);
                log.info("Shutting down {}", Utils.getJdaShardGuildCountString(jda));
                jda.shutdown();
            }
            this.shardManager.shutdown();
            this.shardManager = null;
            this.jdaInstanceHashMap.clear();
            log.info("Shutting down completed");
        }
    }

}