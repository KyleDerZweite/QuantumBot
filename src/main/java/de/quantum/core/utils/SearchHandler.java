package de.quantum.core.utils;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import java.util.List;

public class SearchHandler {

    public static ClosestMatch getClosestMatchingMemberByName(Guild guild, String name) {
        List<Member> members = guild.getMembers();
        Member closestMatch = null;
        int closestDistance = Integer.MAX_VALUE;

        for (Member member : members) {
            String memberName = member.getUser().getName();
            String effectiveName = member.getEffectiveName();

            if (effectiveName.equals(name) || memberName.equals(name)) {
                closestMatch = member;
                closestDistance = -1;
                continue;
            } else if (effectiveName.equalsIgnoreCase(name) || memberName.equalsIgnoreCase(name)) {
                closestMatch = member;
                closestDistance = 0;
                continue;
            } else if (memberName.toLowerCase().contains(name.toLowerCase()) || effectiveName.toLowerCase().contains(name.toLowerCase())) {
                closestMatch = member;
                closestDistance = 1;
                continue;
            }

            // Calculate the Levenshtein distance between the input name and the member's name
            int distance = LevenshteinDistance.levenshteinDistance(name, memberName);
            int effectiveDistance = LevenshteinDistance.levenshteinDistance(name, effectiveName);

            // Check if the member's name or effective name is a closer match
            if (distance < closestDistance || effectiveDistance < closestDistance) {
                if (distance < effectiveDistance) {
                    closestMatch = member;
                    closestDistance = distance + 1;
                } else {
                    closestMatch = member;
                    closestDistance = effectiveDistance + 1;
                }
            }
        }
        return new ClosestMatch(closestMatch, closestDistance);
    }

    public static ClosestMatch getClosestMatchingRoleByName(Guild guild, String name) {
        List<Role> roles = guild.getRoles();
        Role closestMatch = null;
        int closestDistance = Integer.MAX_VALUE;

        for (Role role : roles) {
            String roleName = role.getName();

            if (roleName.equals(name)) {
                closestMatch = role;
                closestDistance = -1;
                continue;
            } else if (roleName.equalsIgnoreCase(name)) {
                closestMatch = role;
                closestDistance = 0;
                continue;
            } else if (roleName.toLowerCase().contains(name.toLowerCase())) {
                closestMatch = role;
                closestDistance = 1;
                continue;
            }

            // Calculate the Levenshtein distance between the input name and the role's name
            int distance = LevenshteinDistance.levenshteinDistance(name, roleName);

            // Check if the role's name is a closer match
            if (distance < closestDistance) {
                closestMatch = role;
                closestDistance = distance + 1;
            }
        }
        return new ClosestMatch(closestMatch, closestDistance);
    }

    public static ClosestMatch getClosestMatchingGuildChannelByName(Guild guild, String name) {
        List<GuildChannel> channels = guild.getChannels();
        GuildChannel closestMatch = null;
        int closestDistance = Integer.MAX_VALUE;

        for (GuildChannel channel : channels) {
            String channelName = channel.getName();

            if (channelName.equals(name)) {
                closestMatch = channel;
                closestDistance = -1;
                continue;
            } else if (channelName.equalsIgnoreCase(name)) {
                closestMatch = channel;
                closestDistance = 0;
                continue;
            } else if (channelName.toLowerCase().contains(name.toLowerCase())) {
                closestMatch = channel;
                closestDistance = 1;
                continue;
            }

            // Calculate the Levenshtein distance between the input name and the channel's name
            int distance = LevenshteinDistance.levenshteinDistance(name, channelName);

            // Check if the channel's name is a closer match
            if (distance < closestDistance) {
                closestMatch = channel;
                closestDistance = distance + 1;
            }
        }
        return new ClosestMatch(closestMatch, closestDistance);
    }

    public static IMentionable getClosestMatchingIMentionableByName(Guild guild, String name) {
        ClosestMatch closestMemberMatch = getClosestMatchingMemberByName(guild, name);
        ClosestMatch closestRoleMatch = getClosestMatchingRoleByName(guild, name);
        ClosestMatch closestChannelMatch = getClosestMatchingGuildChannelByName(guild, name);

        System.out.println(closestMemberMatch);
        System.out.println(closestRoleMatch);
        System.out.println(closestChannelMatch);

        if (closestMemberMatch.closestDistance() <= closestRoleMatch.closestDistance() && closestMemberMatch.closestDistance() <= closestChannelMatch.closestDistance()) {
            return (IMentionable) closestMemberMatch.closestMatch();
        } else if (closestRoleMatch.closestDistance() <= closestMemberMatch.closestDistance() && closestRoleMatch.closestDistance() <= closestChannelMatch.closestDistance()) {
            return (IMentionable) closestRoleMatch.closestMatch();
        } else {
            return (IMentionable) closestChannelMatch.closestMatch();
        }
    }

    public record ClosestMatch(Object closestMatch, int closestDistance) {
    }
}
