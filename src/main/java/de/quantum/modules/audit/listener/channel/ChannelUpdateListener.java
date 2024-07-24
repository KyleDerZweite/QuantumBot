package de.quantum.modules.audit.listener.channel;

import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import de.quantum.modules.audit.AuditHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.channel.update.*;

@EventAnnotation(value = {
        ChannelUpdateAppliedTagsEvent.class,
        ChannelUpdateArchivedEvent.class,
        ChannelUpdateArchiveTimestampEvent.class,
        ChannelUpdateAutoArchiveDurationEvent.class,
        ChannelUpdateBitrateEvent.class,
        ChannelUpdateDefaultLayoutEvent.class,
        ChannelUpdateDefaultReactionEvent.class,
        ChannelUpdateDefaultSortOrderEvent.class,
        ChannelUpdateDefaultThreadSlowmodeEvent.class,
        ChannelUpdateFlagsEvent.class,
        ChannelUpdateInvitableEvent.class,
        ChannelUpdateLockedEvent.class,
        ChannelUpdateNameEvent.class,
        ChannelUpdateNSFWEvent.class,
        ChannelUpdateParentEvent.class,
        ChannelUpdatePositionEvent.class,
        ChannelUpdateRegionEvent.class,
        ChannelUpdateSlowmodeEvent.class,
        ChannelUpdateTopicEvent.class,
        ChannelUpdateTypeEvent.class,
        ChannelUpdateUserLimitEvent.class,
        ChannelUpdateVoiceStatusEvent.class,
})
public class ChannelUpdateListener implements EventInterface<GenericChannelUpdateEvent<?>> {

    @Override
    public void perform(GenericChannelUpdateEvent<?> event) {
        Guild guild = event.getGuild();
        if (!AuditHandler.isGuildLogging(guild, event.getClass())) {
            return;
        }

        System.out.println(event.getChannel().getName());
    }
}
