package de.quantum.modules.audit.listener.emoji;

import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import de.quantum.modules.audit.AuditHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.emoji.EmojiAddedEvent;
import net.dv8tion.jda.api.events.emoji.EmojiRemovedEvent;
import net.dv8tion.jda.api.events.emoji.GenericEmojiEvent;

@EventAnnotation({
        EmojiAddedEvent.class,
        EmojiRemovedEvent.class
})
public class EmojiListener implements EventInterface<GenericEmojiEvent> {

    @Override
    public void perform(GenericEmojiEvent event) {
        Guild guild = event.getGuild();
        if (!AuditHandler.isGuildLogging(guild, event.getClass())) {
            return;
        }

        System.out.println(event.getClass().getName());
    }
}