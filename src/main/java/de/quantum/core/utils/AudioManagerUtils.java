package de.quantum.core.utils;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

@Slf4j
public class AudioManagerUtils {

    public static void joinVoiceChannel(JDA jda, String guildId, String voiceChannelId) {
        Guild guild = jda.getGuildById(guildId);
        if (CheckUtils.checkGuildIsNull(guild, guildId)) {
            return;
        }
        assert guild != null;
        AudioManager audioManager = guild.getAudioManager();

        VoiceChannel voiceChannel = guild.getVoiceChannelById(voiceChannelId);
        if (CheckUtils.checkVoiceChannelIsNull(voiceChannel, guild, voiceChannelId)) {
            return;
        }

        audioManager.openAudioConnection(voiceChannel);
        audioManager.setSelfDeafened(true);
        audioManager.setSelfMuted(true);
    }

    public static void leaveVoiceChannel(JDA jda, String guildId) {
        Guild guild = jda.getGuildById(guildId);
        if (CheckUtils.checkGuildIsNull(guild, guildId)) {
            return;
        }
        assert guild != null;
        AudioManager audioManager = guild.getAudioManager();

        if (audioManager.isConnected()) {
            audioManager.closeAudioConnection();
        }
    }
}
