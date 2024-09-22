package de.quantum.core.database;

import de.quantum.core.utils.Secret;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class DatabaseConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                log.error("Sorry, unable to find database.properties");
                System.exit(1);
            }

            // Load the properties file
            properties.load(input);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
    }

    public static String getDbUrl() {
        return properties.getProperty("db.url");
    }

    public static String getDbUsername() {
        try {
            return Secret.decrypt(properties.getProperty("db.username"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static String getDbPassword() {
        try {
            return Secret.decrypt(properties.getProperty("db.password"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
