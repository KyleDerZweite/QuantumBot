package de.quantum.core.entities;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeoutMap<K, V> {

    public static final long DEFAULT_TIMEOUT = 1;
    public static final TimeUnit DEFAULT_UNIT = TimeUnit.HOURS;

    private final ConcurrentHashMap<K, V> map;
    private final ScheduledExecutorService scheduler;

    public TimeoutMap() {
        this.map = new ConcurrentHashMap<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void put(K key, V value, long timeout, TimeUnit unit) {
        map.put(key, value);
        scheduler.schedule(() -> {
            V removedValue = map.remove(key);
            callOnTimeoutRemove(removedValue);
        }, timeout, unit);
    }

    public void put(K key, V value) {
        map.put(key, value);
        scheduler.schedule(() -> {
            V removedValue = map.remove(key);
            callOnTimeoutRemove(removedValue);
        }, DEFAULT_TIMEOUT, DEFAULT_UNIT);
    }

    public V get(K key) {
        return map.get(key);
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    private void callOnTimeoutRemove(Object value) {
        try {
            Method method = value.getClass().getMethod("onTimeoutRemove");
            method.invoke(value);
        } catch (NoSuchMethodException e) {
            // ignore if the method doesn't exist
        } catch (Exception e) {
            // handle other exceptions
        }
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }
}