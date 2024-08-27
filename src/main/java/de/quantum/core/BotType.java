package de.quantum.core;

import lombok.Getter;

public enum BotType {

    MAIN(0),
    CUSTOM(1),
    DUMMY(2),
    UNKNOWN(-1);

    @Getter
    private final int id;

    BotType(int id) {
        this.id = id;
    }

    public static BotType fromId(int id) {
        for (BotType botType : BotType.values()) {
            if (botType.id == id) {
                return botType;
            }
        }
        return UNKNOWN;
    }

}
