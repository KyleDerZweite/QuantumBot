package de.quantum.core.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class StatusUtils {

    public static MessageEmbed getStatusEmbed(@NotNull JDA jda, @NotNull Guild guild, @NotNull Member member) {
        SelfUser bot = jda.getSelfUser();
        Member selfMember = guild.getSelfMember();

        // Fetch and filter accessible channels
        List<TextChannel> accessibleTextChannels = Utils.getAccessibleChannels(guild.getTextChannels(), selfMember);
        List<VoiceChannel> accessibleVoiceChannels = Utils.getAccessibleChannels(guild.getVoiceChannels(), selfMember);
        List<Category> accessibleCategories = Utils.getAccessibleChannels(guild.getCategories(), selfMember);
        List<NewsChannel> accessibleNewsChannels = Utils.getAccessibleChannels(guild.getNewsChannels(), selfMember);
        List<StageChannel> accessibleStageChannels = Utils.getAccessibleChannels(guild.getStageChannels(), selfMember);
        List<ThreadChannel> accessiblePublicThreads = Utils.getAccessibleChannels(
                guild.getThreadChannels().stream().filter(thread -> thread.getType() == ChannelType.GUILD_PUBLIC_THREAD).collect(Collectors.toList()), selfMember);
        List<ThreadChannel> accessiblePrivateThreads = Utils.getAccessibleChannels(
                guild.getThreadChannels().stream().filter(thread -> thread.getType() == ChannelType.GUILD_PRIVATE_THREAD).collect(Collectors.toList()), selfMember);
        List<ThreadChannel> accessibleNewsThreads = Utils.getAccessibleChannels(
                guild.getThreadChannels().stream().filter(thread -> thread.getType() == ChannelType.GUILD_NEWS_THREAD).collect(Collectors.toList()), selfMember);
        List<ForumChannel> accessibleForumChannels = Utils.getAccessibleChannels(guild.getForumChannels(), selfMember);
        List<MediaChannel> accessibleMediaChannels = Utils.getAccessibleChannels(guild.getMediaChannels(), selfMember);

        // Get total counts
        int totalTextChannels = guild.getTextChannels().size();
        int totalVoiceChannels = guild.getVoiceChannels().size();
        int totalCategories = guild.getCategories().size();
        int totalNewsChannels = guild.getNewsChannels().size();
        int totalStageChannels = guild.getStageChannels().size();
        int totalPublicThreads = (int) guild.getThreadChannels().stream().filter(thread -> thread.getType() == ChannelType.GUILD_PUBLIC_THREAD).count();
        int totalPrivateThreads = (int) guild.getThreadChannels().stream().filter(thread -> thread.getType() == ChannelType.GUILD_PRIVATE_THREAD).count();
        int totalNewsThreads = (int) guild.getThreadChannels().stream().filter(thread -> thread.getType() == ChannelType.GUILD_NEWS_THREAD).count();
        int totalForumChannels = guild.getForumChannels().size();
        int totalMediaChannels = guild.getMediaChannels().size();

        // Calculate percentages
        double textChannelPercentage = Utils.calculatePercentage(accessibleTextChannels.size(), totalTextChannels);
        double voiceChannelPercentage = Utils.calculatePercentage(accessibleVoiceChannels.size(), totalVoiceChannels);
        double categoryPercentage = Utils.calculatePercentage(accessibleCategories.size(), totalCategories);
        double newsChannelPercentage = Utils.calculatePercentage(accessibleNewsChannels.size(), totalNewsChannels);
        double stageChannelPercentage = Utils.calculatePercentage(accessibleStageChannels.size(), totalStageChannels);
        double publicThreadPercentage = Utils.calculatePercentage(accessiblePublicThreads.size(), totalPublicThreads);
        double privateThreadPercentage = Utils.calculatePercentage(accessiblePrivateThreads.size(), totalPrivateThreads);
        double newsThreadPercentage = Utils.calculatePercentage(accessibleNewsThreads.size(), totalNewsThreads);
        double forumChannelPercentage = Utils.calculatePercentage(accessibleForumChannels.size(), totalForumChannels);
        double mediaChannelPercentage = Utils.calculatePercentage(accessibleMediaChannels.size(), totalMediaChannels);

        // Bot Roles
        List<Role> roles = selfMember.getRoles();
        // Bot Permissions
        EnumSet<Permission> permissions = selfMember.getPermissions();

        // Owner Info
        Member owner = guild.retrieveOwner().complete();
        String ownerName = owner != null ? owner.getUser().getName() : "Unknown";

        long gatewayPing = jda.getGatewayPing();
        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        long uptimeMillis = rb.getUptime();

        // Creating the Embed
        return new EmbedBuilder()
                .setAuthor(bot.getName(), null, bot.getEffectiveAvatarUrl())
                .setTitle("Bot Status")
                .addField("Guild Name", guild.getName(), true)
                .addField("Guild Region", guild.getLocale().getNativeName(), true)
                .addField("Verification Level", guild.getVerificationLevel().name(), true)
                .addField("Owner", ownerName, true)
                .addField("Total Members", String.valueOf(guild.getMemberCount()), true)
                .addField("Bot Join Date", selfMember.getTimeJoined().toString(), false)
                .addField("Latency", gatewayPing + " ms", true)
                .addField("Uptime", Utils.formatUptime(uptimeMillis), true)
                .addField("Accessible Channels",
                        String.format(
                                """
                                        Text Channels: %d/%d (%.1f%%)
                                        Voice Channels: %d/%d (%.1f%%)
                                        Categories: %d/%d (%.1f%%)
                                        News Channels: %d/%d (%.1f%%)
                                        Stage Channels: %d/%d (%.1f%%)
                                        Public Threads: %d/%d (%.1f%%)
                                        Private Threads: %d/%d (%.1f%%)
                                        News Threads: %d/%d (%.1f%%)
                                        Forum Channels: %d/%d (%.1f%%)
                                        Media Channels: %d/%d (%.1f%%)""",
                                accessibleTextChannels.size(), totalTextChannels, textChannelPercentage,
                                accessibleVoiceChannels.size(), totalVoiceChannels, voiceChannelPercentage,
                                accessibleCategories.size(), totalCategories, categoryPercentage,
                                accessibleNewsChannels.size(), totalNewsChannels, newsChannelPercentage,
                                accessibleStageChannels.size(), totalStageChannels, stageChannelPercentage,
                                accessiblePublicThreads.size(), totalPublicThreads, publicThreadPercentage,
                                accessiblePrivateThreads.size(), totalPrivateThreads, privateThreadPercentage,
                                accessibleNewsThreads.size(), totalNewsThreads, newsThreadPercentage,
                                accessibleForumChannels.size(), totalForumChannels, forumChannelPercentage,
                                accessibleMediaChannels.size(), totalMediaChannels, mediaChannelPercentage), false)
                .addField("Roles", roles.stream()
                        .map(Role::getAsMention)
                        .collect(Collectors.joining(", ")), false)
                .addField("Permissions", permissions.stream()
                        .map(Permission::getName)
                        .collect(Collectors.joining(", ")), false)
                .setColor(selfMember.getColor())
                .setFooter("Requested by " + member.getEffectiveName(), member.getEffectiveAvatarUrl())
                .build();
    }
}
