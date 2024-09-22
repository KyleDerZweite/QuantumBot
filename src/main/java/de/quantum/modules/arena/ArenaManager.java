package de.quantum.modules.arena;

import de.quantum.core.module.ModuleAnnotation;

@ModuleAnnotation(
        moduleName = "Arena",
        moduleDescription = "Arena System for character battles",
        moduleVersion = "v0.0.1",
        moduleAuthorName = "kylederzweite",
        moduleAuthorID = "378542649579143188"
)
public class ArenaManager {
    private static volatile ArenaManager INSTANCE = null;

    private ArenaManager() {
        if (INSTANCE != null) {
            throw new AssertionError(
                    "Another instance of "
                            + ArenaManager.class.getName()
                            + " class already exists, Can't create a new instance.");
        }
    }

    public static ArenaManager getInstance() {
        if (INSTANCE == null) {
            synchronized (ArenaManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ArenaManager();
                }
            }
        }
        return INSTANCE;
    }




}