package de.quantum.core.shutdown;

import de.quantum.core.database.DatabaseManager;
import lombok.extern.slf4j.Slf4j;
import org.reflections8.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.RejectedExecutionException;

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

        // Increment the latch count by the number of shutdown classes
        CountDownLatch latch = new CountDownLatch(shutdownClasses.size());

        for (Class<?> clazz : shutdownClasses) {
            try {
                // Get the getInstance method if it's a singleton class
                Method getInstanceMethod = clazz.getMethod("getInstance");
                var instance = getInstanceMethod.invoke(null); // null because it's a static method

                try {
                    // Ensure the instance implements ShutdownInterface
                    if (instance instanceof ShutdownInterface) {
                        // Use a separate thread to call shutdown() and count down the latch
                        Thread shutdownThread = new Thread(() -> {
                            try {
                                ((ShutdownInterface) instance).shutdown();
                            } catch (RejectedExecutionException e) {
                                log.debug(e.getMessage());
                            } finally {
                                latch.countDown(); // Count down the latch
                            }
                        });
                        shutdownThread.start();
                    } else {
                        log.error("Instance does not implement ShutdownInterface: {}", clazz.getName());
                        latch.countDown(); // Count down the latch if instance doesn't implement ShutdownInterface
                    }
                } catch (Exception e) {
                    log.error("Failed to shutdown class: {}", clazz.getName(), e);
                    try {
                        ShutdownInterface shutdownInterface = (ShutdownInterface) clazz.getDeclaredConstructors()[0].newInstance();
                        // Use a separate thread to call shutdown() and count down the latch
                        Thread shutdownThread = new Thread(() -> {
                            try {
                                shutdownInterface.shutdown();
                            } catch (Exception ex) {
                                log.error(ex.getMessage(), ex);
                            } finally {
                                latch.countDown(); // Count down the latch
                            }
                        });
                        shutdownThread.start();
                    } catch (InstantiationException | IllegalAccessException |
                             InvocationTargetException newInstanceException) {
                        log.error(newInstanceException.getMessage(), newInstanceException);
                        latch.countDown(); // Count down the latch if instance creation fails
                    }
                }
            } catch (Exception e) {
                log.error("Failed to shutdown class: {}", clazz.getName(), e);
                latch.countDown(); // Count down the latch if instance creation fails
            }
        }

        try {
            // Wait for all shutdown tasks to finish
            latch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        try {
            // Close the database connection after all shutdown tasks have finished
            DatabaseManager.getInstance().getConnection().close();
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
    }

}
