package de.luxury.core.events;

import net.dv8tion.jda.api.events.GenericEvent;

public interface EventInterface <T extends GenericEvent> {
    void perform(T event);
}
