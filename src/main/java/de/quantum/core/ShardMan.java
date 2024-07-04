package de.quantum.core;

import de.quantum.core.events.EventReflector;
import de.quantum.core.utils.CheckUtils;
import de.quantum.core.utils.Utils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;
import net.dv8tion.jda.api.utils.ConcurrentSessionController;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.concurrent.Executors;

@Slf4j
public class ShardMan extends ListenerAdapter {

    private static volatile ShardMan INSTANCE = null;

    @Getter
    @Nullable
    private ShardManager shardManager;

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
    }

    public static ShardMan getInstance() {
        init();
        return INSTANCE;
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
            log.info("Shutting down completed");
        }
    }

}