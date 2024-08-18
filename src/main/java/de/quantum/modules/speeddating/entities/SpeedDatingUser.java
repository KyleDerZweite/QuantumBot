package de.quantum.modules.speeddating.entities;

import lombok.Data;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.HashMap;

@Data
public class SpeedDatingUser {

    private int userId;
    private String name;
    private Member member;
    private HashMap<Integer, Integer> matchHistory;
    private VoiceChannel currentVoiceChannel;
    private boolean available;

    public SpeedDatingUser(int userId, String name, Member member) {
        this.userId = userId;
        this.name = name;
        this.member = member;
        this.matchHistory = new HashMap<>();
        this.currentVoiceChannel = null;
        this.available = true;
    }

    public void updateHistory(int userId, int roundCounter) {
        this.matchHistory.put(userId, roundCounter);
    }

    public boolean moveTo(VoiceChannel voiceChannel) {
        this.currentVoiceChannel = voiceChannel;
        if (voiceChannel == null || this.member == null || this.member.getVoiceState() == null || !this.isAvailable()) {
            return false;
        }
        voiceChannel.getGuild().moveVoiceMember(this.member, voiceChannel).queue();
        return true;
    }

    public boolean reconnect() {
        return moveTo(this.currentVoiceChannel);
    }
}
