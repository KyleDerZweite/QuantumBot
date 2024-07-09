package de.quantum.core.utils;

import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.*;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class StatusUtils {

    private final String buttonKeyPrefix;
    private final LinkedList<String> labels;

    public StatusUtils() {
        this("status");
    }

    public StatusUtils(String buttonKeyPrefix) {
        this.buttonKeyPrefix = buttonKeyPrefix;
        this.labels = new LinkedList<>();
        this.labels.add("- General -");
        this.labels.add("- Accessible Channels -");
        this.labels.add("- Roles -");
        this.labels.add("- Permissions -");
    }

    private EmbedBuilder getBaseEmbedBuilder(SelfUser bot, Member selfMember, Member requestingMember) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder
                .setAuthor(bot.getName(), null, bot.getEffectiveAvatarUrl())
                .setTitle("Bot Status")
                .setColor(selfMember.getColor())
                .setTimestamp(new Date().toInstant())
                .setFooter("Requested by " + requestingMember.getEffectiveName(), requestingMember.getEffectiveAvatarUrl());
        return embedBuilder;
    }

    public String getPermissionsValue(EnumSet<Permission> memberPermissions, EnumSet<Permission> allPermissions) {
        StringBuilder permissionValue = new StringBuilder("```diff\n");
        for (Permission permission : allPermissions) {
            if (memberPermissions.contains(permission)) {
                permissionValue.append("+ %s".formatted(permission.getName())).append("\n");
            } else {
                permissionValue.append("- %s".formatted(permission.getName())).append("\n");
            }
        }
        permissionValue.append("```");
        return permissionValue.toString();
    }


    private void setEmbedBuilderPageOne(EmbedBuilder embedBuilder, Member selfMember, Guild guild) {
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

        embedBuilder.addField("Accessible Channels",
                String.format(
                        """
                                ```prolog
                                Text Channels: %d/%d (%.1f%%)
                                Voice Channels: %d/%d (%.1f%%)
                                Categories: %d/%d (%.1f%%)
                                News Channels: %d/%d (%.1f%%)
                                Stage Channels: %d/%d (%.1f%%)
                                Public Threads: %d/%d (%.1f%%)
                                Private Threads: %d/%d (%.1f%%)
                                News Threads: %d/%d (%.1f%%)
                                Forum Channels: %d/%d (%.1f%%)
                                Media Channels: %d/%d (%.1f%%)
                                ```""",
                        accessibleTextChannels.size(), totalTextChannels, textChannelPercentage,
                        accessibleVoiceChannels.size(), totalVoiceChannels, voiceChannelPercentage,
                        accessibleCategories.size(), totalCategories, categoryPercentage,
                        accessibleNewsChannels.size(), totalNewsChannels, newsChannelPercentage,
                        accessibleStageChannels.size(), totalStageChannels, stageChannelPercentage,
                        accessiblePublicThreads.size(), totalPublicThreads, publicThreadPercentage,
                        accessiblePrivateThreads.size(), totalPrivateThreads, privateThreadPercentage,
                        accessibleNewsThreads.size(), totalNewsThreads, newsThreadPercentage,
                        accessibleForumChannels.size(), totalForumChannels, forumChannelPercentage,
                        accessibleMediaChannels.size(), totalMediaChannels, mediaChannelPercentage), false);
    }

    private String getButtonKeyForPage(int page) {
        if (page >= labels.size()) {
            page = 0;
        } else if (page < 0) {
            page = labels.size() - 1;
        }
        return String.format("%s_%d", this.buttonKeyPrefix, page);
    }

    private String getButtonValueForPage(int page) {
        if (page >= labels.size()) {
            page = 0;
        } else if (page < 0) {
            page = labels.size() - 1;
        }
        return labels.get(page);
    }

    private int getPageFromButtonKey(String buttonKey) {
        String[] buttonArgs = buttonKey.split("_");
        return Integer.parseInt(buttonArgs[buttonArgs.length - 1]);
    }

    public MessageEmbed getStatusEmbedPage(@NotNull JDA jda, @NotNull Guild guild, @NotNull Member requestingMember) {
        return getStatusEmbedPage(getButtonKeyForPage(0), jda, guild, requestingMember);
    }

    public MessageEmbed getStatusEmbedPage(String buttonKey, @NotNull JDA jda, @NotNull Guild guild, @NotNull Member requestingMember) {
        SelfUser bot = jda.getSelfUser();
        Member selfMember = guild.getSelfMember();
        int page = getPageFromButtonKey(buttonKey);
        EmbedBuilder embedBuilder = getBaseEmbedBuilder(bot, selfMember, requestingMember);
        switch (page) {
            default -> {
                // Owner Info
                Member owner = guild.retrieveOwner().complete();
                String ownerName = owner != null ? owner.getUser().getAsMention() : "Unknown";

                long gatewayPing = jda.getGatewayPing();
                RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
                long uptimeMillis = rb.getUptime();

                embedBuilder
                        .addField("Guild Name", guild.getName(), true)
                        .addField("Guild Region", guild.getLocale().getNativeName(), true)
                        .addField("Verification Level", guild.getVerificationLevel().name(), true)
                        .addField("Owner", ownerName, true)
                        .addField("Total Members", String.valueOf(guild.getMemberCount()), true)
                        .addField("Bot Join Date", selfMember.getTimeJoined().toString(), false)
                        .addField("Latency", gatewayPing + " ms", true)
                        .addField("Uptime", Utils.formatUptime(uptimeMillis), true);
            }
            case 1 -> setEmbedBuilderPageOne(embedBuilder, selfMember, guild);
            case 2 -> {
                // Bot Roles
                List<Role> roles = selfMember.getRoles();
                embedBuilder.addField("Roles %d/%d (%.1f%%)".formatted(roles.size(), guild.getRoles().size(), Utils.calculatePercentage(roles.size(), guild.getRoles().size())), roles.stream()
                        .map(Role::getAsMention)
                        .collect(Collectors.joining(", ")), false);
            }
            case 3 -> {
                // Bot Permissions
                EnumSet<Permission> memberPermissions = selfMember.getPermissions();
                EnumSet<Permission> allPermissions = Permission.getPermissions(Permission.ALL_PERMISSIONS);
                String permissionsValue = getPermissionsValue(memberPermissions, allPermissions);
                embedBuilder.addField("Permissions %d/%d (%.1f%%)".formatted(memberPermissions.size(), allPermissions.size(), Utils.calculatePercentage(memberPermissions.size(), allPermissions.size())), permissionsValue, false);
            }
        }
        return embedBuilder.build();
    }

    public Button[] getButtonsForPage(String buttonKey) {
        int page = getPageFromButtonKey(buttonKey);
        Button currentButton = new ButtonImpl(getButtonKeyForPage(page), getButtonValueForPage(page), ButtonStyle.SUCCESS, true, null);
        Button previousButton = new ButtonImpl(getButtonKeyForPage(page - 1), getButtonValueForPage(page - 1), ButtonStyle.PRIMARY, false, null);
        Button nextButton = new ButtonImpl(getButtonKeyForPage(page + 1), getButtonValueForPage(page + 1), ButtonStyle.PRIMARY, false, null);
        return new Button[]{previousButton, currentButton, nextButton};
    }

    public MessageCreateBuilder getMessageCreateBuilderForPage(@NotNull JDA jda, @NotNull Guild guild, @NotNull Member requestingMember) {
        return getMessageCreateBuilderForPage(getButtonKeyForPage(0), jda, guild, requestingMember);
    }

    public MessageCreateBuilder getMessageCreateBuilderForPage(String buttonKey, @NotNull JDA jda, @NotNull Guild guild, @NotNull Member requestingMember) {
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        messageBuilder.setEmbeds(getStatusEmbedPage(buttonKey, jda, guild, requestingMember));
        messageBuilder.setActionRow(getButtonsForPage(buttonKey));
        return messageBuilder;
    }

}
