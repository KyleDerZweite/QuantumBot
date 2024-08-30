package de.quantum.modules.speeddating.entities;

import de.quantum.modules.speeddating.PairSelector;
import de.quantum.modules.speeddating.SpeedDatingDatabaseManager;
import de.quantum.modules.speeddating.SpeedDatingManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class SpeedDatingEvent {

    private int roundCounter = 0;
    private boolean isRunning = true;

    private final Guild guild;
    private final SpeedDatingConfig speedDatingConfig;
    private final ConcurrentHashMap<String, SpeedDatingUser> speedDatingUserMap;

    private final LinkedList<VoiceChannel> createdVoiceChannels;
    private final Thread eventThread;

    public SpeedDatingEvent(Guild guild) {
        this.guild = guild;
        this.speedDatingUserMap = new ConcurrentHashMap<>();
        this.createdVoiceChannels = new LinkedList<>();
        this.eventThread = new Thread(this::startEvent);
        this.speedDatingConfig = SpeedDatingDatabaseManager.getSpeedDatingConfig(guild.getId());

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

    public ConcurrentHashMap<String, SpeedDatingUser> getAvailableSpeedDatingUserMap() {
        ConcurrentHashMap<String, SpeedDatingUser> availableSpeedDatingUserMap = new ConcurrentHashMap<>();
        speedDatingUserMap.forEach((id, user) -> {
            if (user.isAvailable()) {
                availableSpeedDatingUserMap.put(id, user);
            }
        });
        return availableSpeedDatingUserMap;
    }

    public void updateUsers(List<String[]> pairs) {
        for (String[] pair : pairs) {
            String userId1 = pair[0];
            String userId2 = pair[1];
            speedDatingUserMap.get(userId1).updateHistory(userId2, roundCounter);
            speedDatingUserMap.get(userId2).updateHistory(userId1, roundCounter);
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

        PairSelector.MatchingResult matchingResult = PairSelector.getBestMatches(availableSpeedDatingUserMap);
        List<String[]> pairs = matchingResult.pairs();
        List<String> finalUnpairedUsers = matchingResult.finalUnpairedUsers();
        int zeroPairsCount = matchingResult.zeroPairsCount();

        System.out.println(roundCounter);
        System.out.println(pairs);
        System.out.println(finalUnpairedUsers);
        System.out.println(zeroPairsCount);
        System.out.println("--------------------------------------------------");

        updateUsers(pairs);
    }

    public void startEvent() {
        ReentrantLock lock = new ReentrantLock();
        Condition roundCondition = lock.newCondition();

        while (isRunning && !eventThread.isInterrupted()) {
            try {
                prepareMemberMap();
                prepareChannels();
                runNextRound();
                lock.lock();
                try {
                    if (!roundCondition.await(speedDatingConfig.durationSeconds(), TimeUnit.SECONDS)) {
                        stopEvent();
                    }
                } finally {
                    lock.unlock();
                }
                if (!isRunning) {
                    break;
                }
                finishRound();
                roundCounter++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn(e.getMessage(), e);
            }
        }
    }

    public void stopEvent() {
        isRunning = false;
        eventThread.interrupt();
        cleanUp();
        SpeedDatingManager.getInstance().getActiveSpeedDatingMap().remove(guild.getId());
    }

    public void cleanUp() {
        finishRound();
        for (VoiceChannel voiceChannel : createdVoiceChannels) {
            voiceChannel.delete().queue();
        }
    }
}
