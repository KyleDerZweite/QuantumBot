package de.quantum.core.utils;

import de.quantum.core.database.DatabaseManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TeamMember;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.slf4j.helpers.CheckReturnValue;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CheckUtils {

    @CheckReturnValue
    public static boolean checkToken(String token) {
        if (token == null || !token.contains("Bot ")) {
            return false;
        }
        token = token.replace("Bot ", "");
        token = Secret.encrypt(token);
        return DatabaseManager.getInstance().getVerifiedTokens().contains(token);
    }

    @CheckReturnValue
    public static boolean checkNull(Object object) {
        return object == null;
    }

    @CheckReturnValue
    public static boolean checkAnyNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                return true;
            }
        }
        return false;
    }

    @CheckReturnValue
    public static boolean checkNotNull(Object object) {
        return object != null;
    }

    @CheckReturnValue
    public static ArrayList<Permission> getMissingPermissions(JDA jda, Member member, GuildChannel channel, Permission... permissions) {
        if (member == null) {
            return null;
        }
        if (channel.getGuild().getOwnerId().equals(member.getId())) {
            return new ArrayList<>();
        }

        ArrayList<Permission> missingPermissions = new ArrayList<>();
        for (Permission permission : permissions) {
            if (!member.hasPermission(permission) && !member.hasPermission(channel, permission)) {
                missingPermissions.add(permission);
            }
        }
        return missingPermissions;
    }

    @CheckReturnValue
    public static boolean checkGuildIsNull(Guild guild, String guildId) {
        if (checkNull(guild)) {
            log.error("Could not find guild with ID {}", guildId);
            return true;
        }
        return false;
    }

    @CheckReturnValue
    public static boolean checkVoiceChannelIsNull(VoiceChannel voiceChannel, Guild guild, String voiceChannelId) {
        if (checkNull(voiceChannel)) {
            log.error("Could not find voice channel with ID {} in {}", voiceChannelId, guild.getName());
            return true;
        }
        return false;
    }

    @CheckReturnValue
    public static boolean isDigit(String s) {
        return s.matches("\\d+");
    }

}
