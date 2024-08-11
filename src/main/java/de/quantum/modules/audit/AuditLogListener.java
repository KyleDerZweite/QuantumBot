package de.quantum.modules.audit;

import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

@EventAnnotation
public class AuditLogListener implements EventInterface<GuildAuditLogEntryCreateEvent> {
    @Override
    public void perform(GuildAuditLogEntryCreateEvent event) {
        AuditHandler.getInstance().cacheLog(event.getGuild(), new LogEntry(event.getEntry()));
    }
}
