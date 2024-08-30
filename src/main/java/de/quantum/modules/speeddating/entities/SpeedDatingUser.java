package de.quantum.modules.speeddating.entities;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;

@Slf4j
@Data
public class SpeedDatingUser {

    private final Member member;
    private ConcurrentHashMap<String, Integer> matchHistory;
    private VoiceChannel currentVoiceChannel;
    private boolean available = false;

    private final LinkedList<String> lastThreeMatches;

    private final String testUserId;

    public SpeedDatingUser(String testUserId) {
        this.member = null;
        this.matchHistory = new ConcurrentHashMap<>();
        this.currentVoiceChannel = null;
        this.testUserId = testUserId;
        this.lastThreeMatches = new LinkedList<>();
    }

    public SpeedDatingUser(Member member) {
        this.member = member;
        this.matchHistory = new ConcurrentHashMap<>();
        this.currentVoiceChannel = null;
        this.testUserId = null;
        this.lastThreeMatches = new LinkedList<>();
    }

    public void updateHistory(String userId, int roundCounter) {
        this.matchHistory.put(userId, roundCounter);
        if (this.lastThreeMatches.size() > 3) {
            this.lastThreeMatches.removeFirst();
        }
        this.lastThreeMatches.addLast(userId);
    }

    public int getRoundMatched(String userId) {
        return this.matchHistory.getOrDefault(userId, -1);
    }

    public boolean moveTo(VoiceChannel voiceChannel) {
        this.currentVoiceChannel = voiceChannel;
        if (voiceChannel == null || this.member == null || this.member.getVoiceState() == null || !this.isAvailable()) {
            return false;
        }
        try {
            assert this.member != null;
            voiceChannel.getGuild().moveVoiceMember(this.member, voiceChannel).queue();
        } catch (IllegalStateException | RejectedExecutionException e) {
            log.debug(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean reconnect() {
        return moveTo(this.currentVoiceChannel);
    }

    public void initUserId(String userId) {
        if (!matchHistory.containsKey(userId)) {
            matchHistory.put(userId, 0);
        }
    }

    @NotNull
    public String getUserId() {
        if (this.member == null) {
            return Objects.requireNonNullElse(this.testUserId, "null");
        }
        return this.member.getId();
    }

}
