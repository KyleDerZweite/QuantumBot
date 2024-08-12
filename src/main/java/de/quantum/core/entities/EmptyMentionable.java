package de.quantum.core.entities;

import net.dv8tion.jda.api.audit.TargetType;
import net.dv8tion.jda.api.entities.IMentionable;
import org.jetbrains.annotations.NotNull;

public class EmptyMentionable implements IMentionable {

    private final long idLong;
    private final TargetType targetType;

    public EmptyMentionable(String id, TargetType targetType) {
        super();
        this.idLong = Long.parseLong(id);
        this.targetType = targetType;
    }

    public EmptyMentionable(String id) {
        super();
        this.idLong = Long.parseLong(id);
        this.targetType = TargetType.UNKNOWN;
    }

    @NotNull
    @Override
    public String getAsMention() {
        return "%s(%s)".formatted(targetType.name(), idLong);
    }

    @Override
    public long getIdLong() {
        return idLong;
    }
}
