package de.quantum.modules.audit.listener.channel;

import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import de.quantum.modules.audit.AuditHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.channel.forum.ForumTagAddEvent;
import net.dv8tion.jda.api.events.channel.forum.ForumTagRemoveEvent;
import net.dv8tion.jda.api.events.channel.forum.GenericForumTagEvent;

@EventAnnotation(value = {
        ForumTagAddEvent.class,
        ForumTagRemoveEvent.class,
})
public class ForumTagListener implements EventInterface<GenericForumTagEvent> {

    @Override
    public void perform(GenericForumTagEvent event) {
        Guild guild = event.getChannel().getGuild();
        if (!AuditHandler.isGuildLogging(guild, event.getClass())) {
            return;
        }

        System.out.println(event.getChannel().getName());
    }
}