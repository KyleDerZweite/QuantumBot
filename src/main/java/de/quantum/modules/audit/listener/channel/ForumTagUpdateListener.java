package de.quantum.modules.audit.listener.channel;

import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import de.quantum.modules.audit.AuditHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.channel.forum.update.ForumTagUpdateEmojiEvent;
import net.dv8tion.jda.api.events.channel.forum.update.ForumTagUpdateModeratedEvent;
import net.dv8tion.jda.api.events.channel.forum.update.ForumTagUpdateNameEvent;
import net.dv8tion.jda.api.events.channel.forum.update.GenericForumTagUpdateEvent;

@EventAnnotation(value = {
        ForumTagUpdateEmojiEvent.class,
        ForumTagUpdateModeratedEvent.class,
        ForumTagUpdateNameEvent.class,
        GenericForumTagUpdateEvent.class
})
public class ForumTagUpdateListener implements EventInterface<GenericForumTagUpdateEvent<?>> {

    @Override
    public void perform(GenericForumTagUpdateEvent<?> event) {
        Guild guild = event.getChannel().getGuild();
        if (!AuditHandler.isGuildLogging(guild, event.getClass())) {
            return;
        }

        System.out.println(event.getChannel().getName());
    }
}