package de.luxury.core.events;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

@Slf4j
public class EventReflector extends ListenerAdapter {

    @Override
    public void onGenericEvent(@NotNull GenericEvent event) {
        if (EventManager.getInstance().getEventControllerHashMap().containsKey(event.getClass())) {
            LinkedList<EventInterface<? extends GenericEvent>> interfaces = EventManager.getInstance().getEventControllerHashMap().get(event.getClass());
            for (EventInterface<? extends GenericEvent> eventController : interfaces) {
                try {
                    Method m = eventController.getClass().getDeclaredMethod("perform", GenericEvent.class);
                    m.invoke(eventController, event);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }
}
