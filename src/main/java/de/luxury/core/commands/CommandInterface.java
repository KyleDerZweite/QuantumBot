package de.luxury.core.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public interface CommandInterface<T extends GenericCommandInteractionEvent> {

    CommandDataImpl getCommandData();

    void perform(T event);

    default Permission[] getPermissions() {
        return new Permission[]{Permission.USE_APPLICATION_COMMANDS};
    }
}
