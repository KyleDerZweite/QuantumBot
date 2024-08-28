package de.quantum.modules.speeddating.entities;

import de.quantum.core.ShardMan;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.sql.ResultSet;
import java.sql.SQLException;

public record SpeedDatingConfig(String botId, Guild guild, Category category, VoiceChannel voiceChannel,
                                int durationSeconds) {

    public SpeedDatingConfig(String botId, Guild guild, Category category, VoiceChannel voiceChannel) {
        this(botId, guild, category, voiceChannel, 300);
    }

    public SpeedDatingConfig(String botId, String guildId, String categoryId, String voiceChannelId, int durationSeconds) {
        this(botId,
                ShardMan.getInstance().getJDAInstance(botId).getGuildById(guildId),
                ShardMan.getInstance().getGuildByJdaAndId(botId, guildId).getCategoryById(categoryId),
                ShardMan.getInstance().getGuildByJdaAndId(botId, guildId).getVoiceChannelById(voiceChannelId),
                durationSeconds);
    }

    public static SpeedDatingConfig fromResultSet(ResultSet resultSet) throws SQLException {
        String botId = resultSet.getString("bot_id");
        String guildId = resultSet.getString("guild_id");
        String categoryId = resultSet.getString("category_id");
        String voiceChannelId = resultSet.getString("hub_channel_id");
        int durationSeconds = resultSet.getInt("duration_seconds");
        return new SpeedDatingConfig(botId, guildId, categoryId, voiceChannelId, durationSeconds);
    }
}