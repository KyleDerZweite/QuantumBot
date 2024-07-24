package de.quantum.modules.audit.listener.automod;

import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import de.quantum.modules.audit.AuditHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.automod.*;

@EventAnnotation({
        AutoModExecutionEvent.class,
        AutoModRuleCreateEvent.class,
        AutoModRuleDeleteEvent.class,
        AutoModRuleUpdateEvent.class
})
public class AutoModListener implements EventInterface<GenericAutoModRuleEvent> {
    @Override
    public void perform(GenericAutoModRuleEvent event) {
        Guild guild = event.getRule().getGuild();
        if (!AuditHandler.isGuildLogging(guild, event.getClass())) {
            return;
        }

        System.out.println(event.getClass().getName());
    }
}
