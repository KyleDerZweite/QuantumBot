package de.luxury.modules.listener;

import de.luxury.core.events.EventAnnotation;
import de.luxury.core.events.EventInterface;
import de.luxury.core.utils.CheckUtils;
import de.luxury.core.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.events.session.ReadyEvent;

@Slf4j
@EventAnnotation
public class OnReadyListener implements EventInterface<ReadyEvent> {

    @Override
    public void perform(ReadyEvent event) {
        if (!CheckUtils.checkToken(event.getJDA().getToken())) {
            log.error("Failed to authenticate Bot token: {}", Utils.getJdaShardGuildCountString(event.getJDA()));
            event.getJDA().getPresence().setPresence(OnlineStatus.OFFLINE, null);
            event.getJDA().shutdown();
            event.getJDA().shutdownNow();
            return;
        }
        String readyString = "Successfully started: " + Utils.getJdaShardGuildCountString(event.getJDA());
        event.getJDA().getPresence().setPresence(OnlineStatus.ONLINE, null);
        Utils.log_success(readyString);
    }


}
