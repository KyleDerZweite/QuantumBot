package de.quantum.core.commands;

import lombok.Getter;

@Getter
public enum CommandType {
    PUBLIC(true, false),
    GUILD_ONLY(false, false),
    SUPPORT_GUILD_ONLY(false, true);

    private final boolean dmEnabled;
    private final boolean supportGuildOnly;

    CommandType(boolean dmEnabled, boolean supportGuildOnly) {
        this.dmEnabled = dmEnabled;
        this.supportGuildOnly = supportGuildOnly;
    }

}
