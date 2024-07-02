package de.quantum.core.mixins;

import de.quantum.core.utils.Utils;
import lombok.Data;

import java.util.LinkedList;

public class ActivityMixin {

    private int messageCount;
    private int wordCount;
    private int charCount;
    private long latestMessageMillis;

    private int voiceTime;
    private int mutedTime;
    private int deafenTime;
    private long lastVoiceChannelId;
    private long latestVoiceUpdateMillis;

    private int activityTime;
    private long latestActivityMillis;

    private final LinkedList<MemberActivityData> activityDataLinkedList;

    public ActivityMixin() {
        activityDataLinkedList = new LinkedList<>();
    }

    public void increaseMessageActivity(int wordCount, int charCount) {
        this.messageCount++;
        this.wordCount += wordCount;
        this.charCount += charCount;
        this.latestMessageMillis = Utils.getCurrentMillis();
    }

    public void updateVoiceActivity(int voiceTime, int mutedTime, int deafenTime, long lastVoiceChannelId) {
        this.voiceTime += voiceTime;
        this.mutedTime += mutedTime;
        this.deafenTime += deafenTime;
        this.lastVoiceChannelId = lastVoiceChannelId;
        this.latestVoiceUpdateMillis = Utils.getCurrentMillis();
    }

    public void updateMemberActivity(int activityTime, MemberActivityData activityData) {
        this.activityTime += activityTime;
        this.latestActivityMillis = Utils.getCurrentMillis();
        activityDataLinkedList.addLast(activityData);
    }

    @Data
    public static class MemberActivityData {

        private final String activityName;
        private final long startMillis;
        private final String startTime;
        private long endMillis;

        private long voiceStateStartMillis;
        private long voiceStateEndMillis;

        public MemberActivityData(String activityName, long startMillis) {
            this.activityName = activityName;
            this.startMillis = startMillis;
            this.startTime = Utils.getCurrentTime();
        }
    }

}
