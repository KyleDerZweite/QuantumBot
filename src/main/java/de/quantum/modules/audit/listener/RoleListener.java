package de.quantum.modules.audit.listener;

import de.quantum.core.events.EventInterface;
import de.quantum.modules.audit.AuditHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.emoji.update.GenericEmojiUpdateEvent;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;

public class RoleListener implements EventInterface<GenericGuildEvent> {

    @Override
    public void perform(GenericGuildEvent event) {
        Guild guild = event.getGuild();
        if (!AuditHandler.isGuildLogging(guild, event.getClass())) {
            return;
        }

        System.out.println(event.getClass().getName());
    }
}
