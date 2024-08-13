package de.quantum.core.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import java.util.List;

public class SearchHandler {

    public static Member getClosestMatchingMemberByName(Guild guild, String name) {
        List<Member> members = guild.getMembers();
        Member closestMatch = null;
        int closestDistance = Integer.MAX_VALUE;

        for (Member member : members) {
            String memberName = member.getUser().getName();
            String effectiveName = member.getEffectiveName();

            // Calculate the Levenshtein distance between the input name and the member's name
            int distance = LevenshteinDistance.levenshteinDistanceExactMatchWeighted(name, memberName);
            int effectiveDistance = LevenshteinDistance.levenshteinDistanceExactMatchWeighted(name, effectiveName);

            // Check if the member's name or effective name is a closer match
            if (distance < closestDistance || effectiveDistance < closestDistance) {
                if (distance < effectiveDistance) {
                    closestMatch = member;
                    closestDistance = distance;
                } else {
                    closestMatch = member;
                    closestDistance = effectiveDistance;
                }
            }
        }

        return closestMatch;
    }

    public static Role getClosestMatchingRoleByName(Guild guild, String name) {
        List<Role> roles = guild.getRoles();
        Role closestMatch = null;
        int closestDistance = Integer.MAX_VALUE;

        for (Role role : roles) {
            String roleName = role.getName();

            // Calculate the Levenshtein distance between the input name and the role's name
            int distance = LevenshteinDistance.levenshteinDistanceExactMatchWeighted(name, roleName);

            // Check if the role's name is a closer match
            if (distance < closestDistance) {
                closestMatch = role;
                closestDistance = distance;
            }
        }

        return closestMatch;
    }

    public static GuildChannel getClosestMatchingGuildChannelByName(Guild guild, String name) {
        List<GuildChannel> channels = guild.getChannels();
        GuildChannel closestMatch = null;
        int closestDistance = Integer.MAX_VALUE;

        for (GuildChannel channel : channels) {
            String channelName = channel.getName();

            // Calculate the Levenshtein distance between the input name and the channel's name
            int distance = LevenshteinDistance.levenshteinDistanceExactMatchWeighted(name, channelName);

            // Check if the channel's name is a closer match
            if (distance < closestDistance) {
                closestMatch = channel;
                closestDistance = distance;
            }
        }

        return closestMatch;
    }

    public static IMentionable getClosestMatchingIMentionableByName(Guild guild, String name) {
        Member closestMemberMatch = getClosestMatchingMemberByName(guild, name);
        Role closestRoleMatch = getClosestMatchingRoleByName(guild, name);
        GuildChannel closestChannelMatch = getClosestMatchingGuildChannelByName(guild, name);

        int memberDistance = closestMemberMatch != null ? Math.min(LevenshteinDistance.levenshteinDistanceExactMatchWeighted(name, closestMemberMatch.getUser().getName()), LevenshteinDistance.levenshteinDistanceExactMatchWeighted(name, closestMemberMatch.getEffectiveName())) : Integer.MAX_VALUE;
        int roleDistance = closestRoleMatch != null ? LevenshteinDistance.levenshteinDistanceExactMatchWeighted(name, closestRoleMatch.getName()) : Integer.MAX_VALUE;
        int channelDistance = closestChannelMatch != null ? LevenshteinDistance.levenshteinDistanceExactMatchWeighted(name, closestChannelMatch.getName()) : Integer.MAX_VALUE;

        if (memberDistance <= roleDistance && memberDistance <= channelDistance) {
            return closestMemberMatch;
        } else if (roleDistance <= memberDistance && roleDistance <= channelDistance) {
            return closestRoleMatch;
        } else {
            return closestChannelMatch;
        }
    }

}
