package de.quantum.modules.audit.listener;
import de.quantum.core.events.EventAnnotation;
import de.quantum.core.events.EventInterface;
import de.quantum.modules.audit.AuditHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.emoji.update.GenericEmojiUpdateEvent;
import net.dv8tion.jda.api.events.guild.GenericGuildEvent;
import net.dv8tion.jda.api.events.guild.GuildAuditLogEntryCreateEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateAvatarEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateActivitiesEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateAvatarEvent;

import java.util.Objects;

@EventAnnotation({
        GuildMemberJoinEvent.class,
        GuildMemberRemoveEvent.class,
        GuildMemberUpdateNicknameEvent.class,
        GuildMemberUpdateAvatarEvent.class,
        GuildAuditLogEntryCreateEvent.class,
        GuildMemberRoleAddEvent.class,
        GuildMemberRoleRemoveEvent.class
})
public class GuildListener implements EventInterface<GenericGuildEvent> {

    @Override
    public void perform(GenericGuildEvent event) {
        Guild guild = event.getGuild();
        if (!AuditHandler.isGuildLogging(guild, event.getClass())) {
            return;
        }

        System.out.println(event.getClass().getName());
    }
}
