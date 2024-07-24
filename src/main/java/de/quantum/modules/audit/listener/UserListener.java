package de.quantum.modules.audit.listener;

import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import de.quantum.modules.audit.AuditHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.user.GenericUserEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateActivitiesEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateAvatarEvent;

import java.util.LinkedList;

@EventAnnotation(value = {
        UserUpdateAvatarEvent.class,
        UserUpdateActivitiesEvent.class,
})
public class UserListener implements EventInterface<GenericUserEvent> {

    @Override
    public void perform(GenericUserEvent event) {
        LinkedList<Guild> mutualGuilds = new LinkedList<>();
        for (Guild mutualGuild : event.getUser().getMutualGuilds()) {
            if (AuditHandler.isGuildLogging(mutualGuild, event.getClass())) {
                mutualGuilds.addLast(mutualGuild);
            }
        }
        if (mutualGuilds.isEmpty()) {
            return;
        }


        System.out.println(event.getClass().getName());
    }
}