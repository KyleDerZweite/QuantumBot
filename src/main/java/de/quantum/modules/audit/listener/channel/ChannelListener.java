package de.quantum.modules.audit.listener.channel;

import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import de.quantum.modules.audit.AuditHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.channel.*;

@EventAnnotation(value = {
        ChannelCreateEvent.class,
        ChannelDeleteEvent.class
})
public class ChannelListener implements EventInterface<GenericChannelEvent> {

    @Override
    public void perform(GenericChannelEvent event) {
        Guild guild = event.getGuild();
        if (!AuditHandler.isGuildLogging(guild, event.getClass())) {
            return;
        }

        System.out.println(event.getClass().getName());
    }
}