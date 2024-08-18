package de.quantum.modules.audit;

import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import de.quantum.core.module.ModuleEvent;
import de.quantum.modules.audit.entries.LogEntry;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;

@EventAnnotation
@ModuleEvent(moduleName = "Audit")
public class AuditLogListener implements EventInterface<GuildAuditLogEntryCreateEvent> {
    @Override
    public void perform(GuildAuditLogEntryCreateEvent event) {
        AuditManager.getInstance().cacheLog(event.getGuild(), new LogEntry(event.getEntry()));
    }
}
