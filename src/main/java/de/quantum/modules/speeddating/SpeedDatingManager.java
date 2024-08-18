package de.quantum.modules.speeddating;

import de.quantum.core.module.ModuleAnnotation;
import de.quantum.modules.speeddating.entities.SpeedDatingConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ModuleAnnotation(
        moduleName = "SpeedDating",
        moduleDescription = "SpeedDating system with semi-random matching, ensures everyone gets matched at least once!",
        moduleVersion = "v0.0.1",
        moduleAuthorName = "kylederzweite",
        moduleAuthorID = "378542649579143188"
)
public class SpeedDatingManager {

    private static volatile SpeedDatingManager INSTANCE = null;

    private SpeedDatingManager() {
        if (INSTANCE != null) {
            throw new AssertionError(
                    "Another instance of "
                            + SpeedDatingManager.class.getName()
                            + " class already exists, Can't create a new instance.");
        }
        this.guildConfigMap = SpeedDatingDatabaseManager.getSpeedDatingConfigs();
    }

    public static SpeedDatingManager getInstance() {
        if (INSTANCE == null) {
            synchronized (SpeedDatingManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SpeedDatingManager();
                }
            }
        }
        return INSTANCE;
    }

    @Getter
    private final ConcurrentHashMap<String, SpeedDatingConfig> guildConfigMap;




}
