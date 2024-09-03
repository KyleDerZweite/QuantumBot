package de.quantum.modules.speeddating.entities;

import de.quantum.core.entities.CircularList;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Data
public class SpeedDatingUser {

    private final Member member;
    private ConcurrentHashMap<String, Integer> matchHistory;
    private VoiceChannel currentVoiceChannel;
    private boolean available = false;

    private final CircularList lastThreeMatches;

    private final String testUserId;

    public SpeedDatingUser(String testUserId) {
        this.member = null;
        this.matchHistory = new ConcurrentHashMap<>();
        this.currentVoiceChannel = null;
        this.testUserId = testUserId;
        this.lastThreeMatches = new CircularList(3);
    }

    public SpeedDatingUser(Member member) {
        this.member = member;
        this.matchHistory = new ConcurrentHashMap<>();
        this.currentVoiceChannel = null;
        this.testUserId = null;
        this.lastThreeMatches = new CircularList(3);
    }

    public void updateHistory(String userId, int roundCounter) {
        this.matchHistory.put(userId, roundCounter);
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

    @NotNull
    public String getUserId() {
        if (this.member == null) {
            return Objects.requireNonNullElse(this.testUserId, "null");
        }
        return this.member.getId();
    }

    public LinkedList<String> getBestMatches(@NotNull List<String> users) {
        return users.stream()
                .filter(user -> !user.equals(getUserId()))
                .map(user -> new AbstractMap.SimpleEntry<>(user, this.getRoundMatched(user)))
                .filter(entry -> !lastThreeMatches.contains(entry.getKey()))
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public boolean hasMissingMatch(@NotNull List<String> users) {
        return users.stream()
                .filter(user -> !user.equals(getUserId()))
                .anyMatch(user -> !matchHistory.containsKey(user));
    }

    public int getFilterValue(@NotNull List<String> users) {
        return (int) users.stream()
                .filter(user -> !user.equals(getUserId()))
                .filter(user -> !matchHistory.containsKey(user))
                .count();
    }

}
