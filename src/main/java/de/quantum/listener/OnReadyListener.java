package de.quantum.listener;

import de.quantum.core.ShardMan;
import de.quantum.core.commands.CommandManager;
import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import de.quantum.core.utils.CheckUtils;
import de.quantum.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.session.ReadyEvent;

@Slf4j
@EventAnnotation
public class OnReadyListener implements EventInterface<ReadyEvent> {

    @Override
    public void perform(ReadyEvent event) {
        System.out.printf("%s: %s", event.getJDA().getSelfUser().getAsTag(),event.getJDA().retrieveCommands().complete());
        System.out.println();
        String botId = event.getJDA().getSelfUser().getId();
        if (!CheckUtils.checkToken(event.getJDA().getToken())) {
            log.error("Failed to authenticate Bot token: {}", Utils.getJdaShardGuildCountString(event.getJDA()));
            event.getJDA().getPresence().setPresence(OnlineStatus.OFFLINE, null);
            event.getJDA().shutdown();
            event.getJDA().shutdownNow();
            return;
        } else if (ShardMan.getInstance().getJdaInstanceHashMap().containsKey(botId)) {
            log.warn("An instance of this bot is already running: {}", Utils.getJdaShardGuildCountString(event.getJDA()));
            event.getJDA().shutdown();
            event.getJDA().shutdownNow();
            return;
        }

        ShardMan.getInstance().getJdaInstanceHashMap().put(botId, event.getJDA());
        log.info("Registering Commands for {}", Utils.getJdaShardGuildCountString(event.getJDA()));
        CommandManager.getInstance().registerCommands(event.getJDA());
        log.info("Deleting unused Commands for {}", Utils.getJdaShardGuildCountString(event.getJDA()));
        CommandManager.getInstance().deleteUnusedCommands(event.getJDA());

        event.getJDA().getPresence().setPresence(OnlineStatus.ONLINE, null);
        String readyString = "Successfully started: " + Utils.getJdaShardGuildCountString(event.getJDA());
        log.info(readyString);
    }
}
