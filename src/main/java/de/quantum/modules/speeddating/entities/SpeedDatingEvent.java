package de.quantum.modules.speeddating.entities;

import de.quantum.modules.speeddating.SpeedDatingDatabaseManager;
import de.quantum.modules.speeddating.SpeedDatingManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.requests.RestAction;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SpeedDatingEvent {

    private int roundCounter = 0;
    private boolean isRunning = true;

    private final Guild guild;
    private final SpeedDatingConfig speedDatingConfig;
    private final ConcurrentHashMap<String, SpeedDatingUser> speedDatingUserMap;

    private final LinkedList<VoiceChannel> createdVoiceChannels;
    private final Thread eventThread;

    private final Set<String> waitingList;

    private String oldChannelName;

    public SpeedDatingEvent(Guild guild) {
        this.guild = guild;
        this.speedDatingUserMap = new ConcurrentHashMap<>();
        this.createdVoiceChannels = new LinkedList<>();
        this.eventThread = new Thread(this::startEvent);
        this.speedDatingConfig = SpeedDatingDatabaseManager.getSpeedDatingConfig(guild.getId());
        this.waitingList = new HashSet<>();

        eventThread.start();
    }


    public void prepareMemberMap() {
        speedDatingConfig.voiceChannel().getMembers().forEach(member -> {
            if (speedDatingUserMap.containsKey(member.getId())) {
                speedDatingUserMap.get(member.getId()).setAvailable(true);
            } else {
                SpeedDatingUser user = new SpeedDatingUser(member);
                user.setAvailable(true);
                speedDatingUserMap.put(member.getId(), user);
            }
        });
    }

    public void prepareChannels() {
        ConcurrentHashMap<String, SpeedDatingUser> availableSpeedDatingUserMap = getAvailableSpeedDatingUserMap();
        int channelCount = Math.floorDiv(availableSpeedDatingUserMap.size(), 2) + 1;
        if (channelCount == createdVoiceChannels.size()) {
            return;
        } else if (createdVoiceChannels.size() < channelCount) {
            int difference = channelCount - createdVoiceChannels.size();
            for (int i = 0; i < difference; i++) {
                VoiceChannel voiceChannel = speedDatingConfig.category()
                        .createVoiceChannel("Group-%s".formatted(createdVoiceChannels.size()))
                        .submit().join();
                createdVoiceChannels.addLast(voiceChannel);
            }
        } else {
            int difference = createdVoiceChannels.size() - channelCount;
            for (int i = 0; i < difference; i++) {
                createdVoiceChannels.removeLast().delete().submit().join();
            }
        }
    }

    public void finishRound() {
        speedDatingUserMap.forEach((id, user) -> {
            user.moveTo(speedDatingConfig.voiceChannel());
            user.setAvailable(false);
        });
    }

    public void runNextRound() {
        ConcurrentHashMap<String, SpeedDatingUser> availableSpeedDatingUserMap = getAvailableSpeedDatingUserMap();
        ConcurrentHashMap<String, String> pairsMap = getBestMatches(availableSpeedDatingUserMap);

        AtomicInteger channelIndex = new AtomicInteger(0);
        pairsMap.forEach((userId1, userId2) -> {
            int currentIndex = channelIndex.getAndIncrement();
            if (currentIndex < createdVoiceChannels.size()) {
                VoiceChannel pairChannel = createdVoiceChannels.get(currentIndex);
                if (availableSpeedDatingUserMap.containsKey(userId1) && availableSpeedDatingUserMap.containsKey(userId2)) {
                    availableSpeedDatingUserMap.get(userId1).moveTo(pairChannel);
                    availableSpeedDatingUserMap.get(userId2).moveTo(pairChannel);
                }
            }
        });
        updateUsers(pairsMap);
    }

    public void startEvent() {
        oldChannelName = speedDatingConfig.voiceChannel().getName();
        try {
            while (isRunning && !eventThread.isInterrupted()) {
                try {
                    prepareMemberMap();
                    prepareChannels();
                    runNextRound();
                    // Wait for the desired duration
                    Thread.sleep(speedDatingConfig.durationSeconds() * 1000L); // Convert seconds to milliseconds
                    if (!hasMissingMatches(getAvailableSpeedDatingUserMap())) {
                        break;
                    }
                    finishRound();
                    roundCounter++;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn(e.getMessage(), e);
                }
            }
        } finally {
            cleanUp();
            SpeedDatingManager.getInstance().getActiveSpeedDatingMap().remove(guild.getId());
        }
    }

    public void stopEvent() {
        isRunning = false;
        if (!eventThread.isInterrupted()) eventThread.interrupt();
    }

    public void cleanUp() {
        try {
            finishRound();
            for (VoiceChannel voiceChannel : createdVoiceChannels) {
                try {
                    voiceChannel.delete().queue();
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }
        } catch (RejectedExecutionException e) {
            log.debug(e.getMessage());
        }
    }

    public ConcurrentHashMap<String, SpeedDatingUser> getAvailableSpeedDatingUserMap() {
        ConcurrentHashMap<String, SpeedDatingUser> availableSpeedDatingUserMap = new ConcurrentHashMap<>();
        speedDatingUserMap.forEach((id, user) -> {
            if (user.isAvailable()) {
                availableSpeedDatingUserMap.put(id, user);
            }
        });
        return availableSpeedDatingUserMap;
    }

    private void updateUsers(ConcurrentHashMap<String, String> pairs) {
        pairs.forEach((userId1, userId2) -> {
            speedDatingUserMap.get(userId1).updateHistory(userId2, roundCounter);
            speedDatingUserMap.get(userId2).updateHistory(userId1, roundCounter);
        });

        // Clear the waiting list
        waitingList.clear();
        // Add unpaired users to the waiting list
        speedDatingUserMap.keySet().forEach(userId -> {
            if (!pairs.containsKey(userId) && !pairs.containsValue(userId)) {
                waitingList.add(userId);
            }
        });
    }

    public ConcurrentHashMap<String, String> getBestMatches(ConcurrentHashMap<String, SpeedDatingUser> tMap) {
        List<String> users = new ArrayList<>(tMap.keySet());
        ConcurrentHashMap<String, LinkedList<String>> bestPossibleMatches = new ConcurrentHashMap<>();
        tMap.forEach((userId, speedDatingUser) -> bestPossibleMatches.put(userId, speedDatingUser.getBestMatches(users)));

        ConcurrentHashMap<String, String> pairsMap = new ConcurrentHashMap<>();

        // Sort users by the number of available matches
        List<String> sortedUsers = new ArrayList<>(bestPossibleMatches.keySet());
        sortedUsers.sort((u1, u2) -> {
            int u1Waiting = waitingList.contains(u1) ? 0 : 1;
            int u2Waiting = waitingList.contains(u2) ? 0 : 1;
            if (u1Waiting != u2Waiting) {
                return u1Waiting - u2Waiting;
            } else {
                return tMap.get(u2).getFilterValue(users) - tMap.get(u1).getFilterValue(users);
            }
        });

        for (String userId : sortedUsers) {
            LinkedList<String> matches = bestPossibleMatches.get(userId);
            for (String matchId : matches) {
                if (!pairsMap.containsKey(userId) && !pairsMap.containsKey(matchId) && !pairsMap.containsValue(userId) && !pairsMap.containsValue(matchId)) {
                    pairsMap.put(userId, matchId);
                    break;
                }
            }
        }
        return pairsMap;
    }

    public boolean hasMissingMatches(ConcurrentHashMap<String, SpeedDatingUser> tMap) {
        List<String> users = new ArrayList<>(tMap.keySet());
        return tMap.entrySet().stream().anyMatch((entry) -> entry.getValue().hasMissingMatch(users));
    }

    public int estimateRounds(int n) {
        if (n <= 2) {
            return n;
        } else {
            return estimateRounds(n / 2) + estimateRounds(n - n / 2);
        }
    }


}
