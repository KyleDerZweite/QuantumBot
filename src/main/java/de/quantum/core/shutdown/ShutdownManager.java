package de.quantum.core.shutdown;

import de.quantum.core.database.DatabaseManager;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.LinkedList;

@Slf4j
public class ShutdownManager {

    public static void shutdown() {
        LinkedList<Class<?>> shutdownClasses = new LinkedList<>();

        for (Class<?> clazz : new Reflections().getTypesAnnotatedWith(ShutdownAnnotation.class)) {
            ShutdownAnnotation annotation = clazz.getAnnotation(ShutdownAnnotation.class);
            if (annotation.shutdownLast()) {
                shutdownClasses.addLast(clazz);
            } else {
                shutdownClasses.addFirst(clazz);
            }
        }

        for (Class<?> clazz : shutdownClasses) {
            try {
                // Get the getInstance method if it's a singleton class
                Method getInstanceMethod = clazz.getMethod("getInstance");
                var instance = getInstanceMethod.invoke(null); // null because it's a static method

                // Ensure the instance implements ShutdownInterface
                if (instance instanceof ShutdownInterface) {
                    ((ShutdownInterface) instance).shutdown();
                } else {
                    log.error("Instance does not implement ShutdownInterface: {}", clazz.getName());
                }
            } catch (Exception e) {
                log.error("Failed to shutdown class: {}", clazz.getName(), e);
                try {
                    ShutdownInterface shutdownInterface = (ShutdownInterface) clazz.getDeclaredConstructors()[0].newInstance();
                    shutdownInterface.shutdown();
                } catch (InstantiationException | IllegalAccessException |
                         InvocationTargetException newInstanceException) {
                    log.error(newInstanceException.getMessage(), newInstanceException);
                }
            }
        }

//        try {
//            DatabaseManager.getInstance().getConnection().close();
//        } catch (SQLException e) {
//            log.error(e.getMessage(), e);
//        }

    }

}
