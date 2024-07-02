package de.quantum.core.events;

import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.GenericEvent;
import org.reflections8.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.LinkedList;

@Slf4j
public class EventManager {
    private static volatile EventManager INSTANCE = null;

    private HashMap<Class<?>, LinkedList<EventInterface<? extends GenericEvent>>> eventControllerHashMap = null;

    private EventManager() {
        if (INSTANCE != null) {
            throw new AssertionError(
                    "Another instance of "
                            + EventManager.class.getName()
                            + " class already exists, Can't create a new instance.");
        }
    }

    public void load() {
        eventControllerHashMap = new HashMap<>();
        for (Class<?> clazz : new Reflections().getTypesAnnotatedWith(EventAnnotation.class)) {
            try {
                EventInterface<? extends GenericEvent> eventInterface = (EventInterface<? extends GenericEvent>) clazz.getDeclaredConstructors()[0].newInstance();
                // Gets the type of the listener by looking up the class type of the generic parameter
                ParameterizedType parameterizedType = (ParameterizedType) eventInterface.getClass().getGenericInterfaces()[0];

                Class<?> eventType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                addEventType(eventType, eventInterface);

                Class<?>[] eventTypes = clazz.getAnnotation(EventAnnotation.class).value();
                if (eventTypes.length == 0) {
                    continue;
                }
                for (Class<?> type : eventTypes) {
                    addEventType(type, eventInterface);
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public HashMap<Class<?>, LinkedList<EventInterface<? extends GenericEvent>>> getEventControllerHashMap() {
        if (eventControllerHashMap == null) {
            load();
        }
        return eventControllerHashMap;
    }

    private void addEventType(Class<?> eventType, EventInterface<? extends GenericEvent> eventInterface) {
        if (eventControllerHashMap.containsKey(eventType)) {
            eventControllerHashMap.get(eventType).add(eventInterface);
        } else {
            LinkedList<EventInterface<? extends GenericEvent>> interfaces = new LinkedList<>();
            interfaces.add(eventInterface);
            eventControllerHashMap.put(eventType, interfaces);
        }
    }


    public static EventManager getInstance() {
        if (INSTANCE == null) {
            synchronized (EventManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EventManager();
                }
            }
        }
        return INSTANCE;
    }
}